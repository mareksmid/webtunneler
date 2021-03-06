package cz.mareksmid.webtunneler.server2;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import cz.mareksmid.webtunneler.server2.json.UpdatePacket;
import cz.mareksmid.webtunneler.server2.json.Bullet;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class Scene implements ActionListener {

    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();


    private int t1x, t1y, t2x, t2y;
    private int or1, or2;
    private int t1h, t2h;
    private int t1e, t2e;
    private int t1d = 0, t2d = 0;
    
    private int exploded = 0;
    
    private int b1x, b1y, b2x, b2y;
    private List<Polygon> stones;
    
    
    private final boolean[][] dirt = new boolean[Const.DIRT_X_CNT][Const.DIRT_Y_CNT];
    private static final Polygon[][] dirtPoly = new Polygon[Const.DIRT_X_CNT][Const.DIRT_Y_CNT];
    private final Set<Point> dirtUpdate1, dirtUpdate2;
    private final Set<Bullet> bullets1, bullets2;
    private final Set<Bullet> bulletsUpdate1, bulletsUpdate2;
    private final Set<Integer> bulletsRemoved1, bulletsRemoved2;
    private final Set<Integer> ebulletsRemoved1, ebulletsRemoved2;
    
    private final Timer bulletTimer = new Timer(Const.BULLET_INTERVAL, this);
    
    static {
        for (int x = 0; x < Const.DIRT_X_CNT; x++) {
            for (int y = 0; y < Const.DIRT_Y_CNT; y++) {
                Coordinate[] dp = {
                    new Coordinate(x*Const.DIRT_W, y*Const.DIRT_H),
                    new Coordinate((x+1)*Const.DIRT_W, y*Const.DIRT_H),
                    new Coordinate((x+1)*Const.DIRT_W, (y+1)*Const.DIRT_H),
                    new Coordinate(x*Const.DIRT_W, (y+1)*Const.DIRT_H),
                    new Coordinate(x*Const.DIRT_W, y*Const.DIRT_H)};
                dirtPoly[x][y] = createPolygon(dp);
            }
        }
    }
    
    public Scene(int b1x, int b1y, int b2x, int b2y, List<Polygon> stones) {
        this.b1x = b1x;
        this.b1y = b1y;
        this.b2x = b2x;
        this.b2y = b2y;
        this.stones = stones;
        
        dirtUpdate1 = Collections.synchronizedSet(new HashSet<Point>());
        dirtUpdate2 = Collections.synchronizedSet(new HashSet<Point>());
        bullets1 = Collections.synchronizedSet(new HashSet<Bullet>());
        bullets2 = Collections.synchronizedSet(new HashSet<Bullet>());
        bulletsUpdate1 = Collections.synchronizedSet(new HashSet<Bullet>());
        bulletsUpdate2 = Collections.synchronizedSet(new HashSet<Bullet>());
        bulletsRemoved1 = new HashSet<Integer>();
        ebulletsRemoved1 = new HashSet<Integer>();
        bulletsRemoved2 = new HashSet<Integer>();
        ebulletsRemoved2 = new HashSet<Integer>();
    }
        

    public UpdatePacket update(PosPacket p, boolean first) {
        int[] x = {p.getX(), p.getY()}, ox;
        if (first) {
            or1 = p.getOr();
            ox = new int[] {t1x, t1y};
        } else {
            or2 = p.getOr();
            ox = new int[] {t2x, t2y};
        }
        if (!checkCollisions(ox, x, first)) {
            if (first) {
                t1x = x[0]; t1y = x[1];
            } else {
                t2x = x[0]; t2y = x[1];
            }
        }
        if (first) synchronized(bullets1) {
            for (Bullet b : p.getB()) {
                if (t1e >= Const.BULLET_ENERGY) {
                    bullets1.add(b);
                    bulletsUpdate2.add(b);
                    t1e -= Const.BULLET_ENERGY;
                }
            }
        } else synchronized(bullets2) {
            for (Bullet b : p.getB()) {
                if (t2e >= Const.BULLET_ENERGY) {
                    bullets2.add(b);
                    bulletsUpdate1.add(b);
                    t2e -= Const.BULLET_ENERGY;
                }
            }
        }
        UpdatePacket up;
        if (first) synchronized(dirtUpdate1) {
            up = new UpdatePacket(t1x, t1y, t1h, t1e, or2, t2x, t2y, bulletsUpdate1, new HashSet<Point>(dirtUpdate1), new HashSet<Integer>(bulletsRemoved1), new HashSet<Integer>(ebulletsRemoved1));
            dirtUpdate1.clear();
            bulletsUpdate1.clear();
            bulletsRemoved1.clear();
            ebulletsRemoved1.clear();
        } else synchronized(dirtUpdate2) {
            up = new UpdatePacket(t2x, t2y, t2h, t2e, or1, t1x, t1y, bulletsUpdate2, new HashSet<Point>(dirtUpdate2), new HashSet<Integer>(bulletsRemoved2), new HashSet<Integer>(ebulletsRemoved2));
            dirtUpdate2.clear();
            bulletsUpdate2.clear();
            bulletsRemoved2.clear();
            ebulletsRemoved2.clear();
        }
        return up;
    }

    private boolean checkCollisions(int[] ox, int[] x, boolean first) {
        boolean c = false;
        Polygon t, et;
        if (first) {t = Const.TANK_POLYS[or1]; et = Const.TANK_POLYS[or2];}
        else {t = Const.TANK_POLYS[or2]; et = Const.TANK_POLYS[or1];}
        AffineTransformation tr = AffineTransformation.translationInstance(x[0], x[1]);
        AffineTransformation etr;
        t = (Polygon) tr.transform(t);
        if (first) {etr = AffineTransformation.translationInstance(t2x, t2y);}
        else {etr = AffineTransformation.translationInstance(t1x, t1y);}
        et = (Polygon) etr.transform(et);

        if (!c) for (Polygon s : stones) {
            if (t.intersects(s)) {
                c = true;
                break;
            }
        }

        if (!c) {
            if (t.intersects(et)) {
                c = true;
            }
        }

        if (!c) {
            if (collidesBaseWalls(t, b1x, b1y)) {c = true;}
            else if (collidesBaseWalls(t, b2x, b2y)) {c = true;}
        }
        
        if (!c) {
            int dx = x[0]/Const.DIRT_W, dy = x[1]/Const.DIRT_H;
            boolean dc = false;
            for (int i = Math.max(0, dx-2); i < Math.min(Const.DIRT_X_CNT, dx+2); i++) for (int j = Math.max(0, dy-2); j < Math.min(Const.DIRT_Y_CNT, dy+2); j++) {
                if (dirt[i][j] && t.intersects(dirtPoly[i][j])) {
                    dc = true;
                    break;
                }
            }
            if (dc) {
                x[0] = (ox[0]+x[0])/2;
                x[1] = (ox[1]+x[1])/2;
                dx = x[0]/Const.DIRT_W; dy = x[1]/Const.DIRT_H;
                if (first) {t = Const.TANK_POLYS[or1];} else {t = Const.TANK_POLYS[or2];}
                tr = AffineTransformation.translationInstance(x[0], x[1]);
                t = (Polygon) tr.transform(t);
                for (int i = Math.max(0, dx-2); i < Math.min(Const.DIRT_X_CNT, dx+2); i++) for (int j = Math.max(0, dy-2); j < Math.min(Const.DIRT_Y_CNT, dy+2); j++) {
                    if (dirt[i][j] && t.intersects(dirtPoly[i][j])) {
                        dig(i, j);
                    }
                }
            }
        }
        
        return c;
    }

    public void init() {
        for (int i = 0; i < Const.DIRT_X_CNT; i++) {
            Arrays.fill(dirt[i], true);
            if ((i >= b1x/Const.DIRT_W) && (i < (b1x+Const.BASE_WIDTH)/Const.DIRT_W)) {
               Arrays.fill(dirt[i], b1y/Const.DIRT_H, (b1y+Const.BASE_HEIGHT)/Const.DIRT_H, false);
            }
            if ((i >= b2x/Const.DIRT_W) && (i < (b2x+Const.BASE_WIDTH)/Const.DIRT_W)) {
               Arrays.fill(dirt[i], b2y/Const.DIRT_H, (b2y+Const.BASE_HEIGHT)/Const.DIRT_H, false);
            }
        }
        dirtUpdate1.clear();
        dirtUpdate2.clear();
        synchronized (bullets1) {bullets1.clear();}
        synchronized (bullets2) {bullets2.clear();}
        t1e = Const.MAX_ENERGY;
        t2e = Const.MAX_ENERGY;
        t1h = Const.MAX_HEALTH;
        t2h = Const.MAX_HEALTH;
        
        t1x = b1x + Const.BASE_WIDTH/2;
        t1y = b1y + Const.BASE_HEIGHT/2;
        t2x = b2x + Const.BASE_WIDTH/2;
        t2y = b2y + Const.BASE_HEIGHT/2;
        
        exploded = 0;
        bulletTimer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == bulletTimer) {
            synchronized (bullets1) {
                processBullets(bullets1, bulletsRemoved1, ebulletsRemoved2, true);
            }
            synchronized (bullets2) {
                processBullets(bullets2, bulletsRemoved2, ebulletsRemoved1, false);
            }
            recharge();
        }
    }

    private void processBullets(Set<Bullet> bullets, Set<Integer> bulletsRemoved, Set<Integer> ebulletsRemoved, boolean first) {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            com.vividsolutions.jts.geom.Point p = createPoint(b.getX(), b.getY());
            
            if (collidesTank(!first, p)) {
                hitTank(!first);
                bulletsRemoved.add(b.getId());
                ebulletsRemoved.add(b.getId());
                iter.remove();
            } else if (collidesStone(p) || !move(b)) {
                bulletsRemoved.add(b.getId());
                ebulletsRemoved.add(b.getId());
                iter.remove();
            }
        }
    }
    
    public boolean move(Bullet b) {
        int x = b.getX(), y = b.getY();
        int dx = 0, dy = 0;
        switch (b.getOr()) {
        case 0:
            y -= Const.BULLET_SHOOT_INCR_RECT;
            dy = -1;
            break;
        case 1:
            x += Const.BULLET_SHOOT_INCR_DIAG;
            y -= Const.BULLET_SHOOT_INCR_DIAG;
            dx = +1; dy = -1;
            break;
        case 2:
            x += Const.BULLET_SHOOT_INCR_RECT;
            dx = +1;
            break;
        case 3:
            x += Const.BULLET_SHOOT_INCR_DIAG;
            y += Const.BULLET_SHOOT_INCR_DIAG;
            dx = +1; dy = +1;
            break;
        case 4:
            y += Const.BULLET_SHOOT_INCR_RECT;
            dy = +1;
            break;
        case 5:
            x -= Const.BULLET_SHOOT_INCR_DIAG;
            y += Const.BULLET_SHOOT_INCR_DIAG;
            dx = -1; dy = +1;
            break;
        case 6:
            x -= Const.BULLET_SHOOT_INCR_RECT;
            dx = -1;
            break;
        case 7:
            x -= Const.BULLET_SHOOT_INCR_DIAG;
            y -= Const.BULLET_SHOOT_INCR_DIAG;
            dx = -1; dy = -1;
            break;
        }
        int tx = b.getX() / Const.DIRT_W;
        int ty = b.getY() / Const.DIRT_H;
        int ex = x / Const.DIRT_W;
        int ey = y / Const.DIRT_H;
        while (((tx-ex)*dx<=0) && ((ty-ey)*dy<=0)) {
            if ((tx >= 0) && (ty >= 0) && (tx < Const.ARENA_WIDTH/Const.DIRT_W) && (ty < Const.ARENA_HEIGHT/Const.DIRT_H) && dirt[tx][ty]) {
                dig(tx, ty);
                return false;
            }
            tx += dx; ty += dy;
        }
        b.moveTo(x, y);
        return (x >= 0) && (y >= 0) && (x < Const.ARENA_WIDTH) && (y < Const.ARENA_HEIGHT);
    }
    
    
    private boolean collidesTank(boolean first, Geometry g) {
        Polygon t = Const.TANK_POLYS[first?or1:or2];
        AffineTransformation tr = AffineTransformation.translationInstance(first?t1x:t2x, first?t1y:t2y);
        t = (Polygon) tr.transform(t);
        return t.intersects(g);
    }

    private boolean collidesStone(Geometry g) {
        for (Polygon s : stones) {
            if (s.intersects(g)) {return true;}
        }
        return false;
    }
    
    private void dig(int x, int y) {
        dirt[x][y] = false;
        synchronized(dirtUpdate1) {
            dirtUpdate1.add(new Point(x, y));
        }
        synchronized(dirtUpdate2) {
            dirtUpdate2.add(new Point(x, y));
        }
    }
    
    /*private Bullet createBullet(int or, int x, int y) {
        int posX = x, posY = y;
        switch (or) {
            case 0:
                posY -= Const.TANK_H2;
                break;
            case 1:
                posX += Const.TANK_DIAG;
                posY -= Const.TANK_DIAG;
                break;
            case 2:
                posX += Const.TANK_H2;
                break;
            case 3:
                posX += Const.TANK_DIAG;
                posY += Const.TANK_DIAG;
                break;
            case 4:
                posY += Const.TANK_H2;
                break;
            case 5:
                posX -= Const.TANK_DIAG;
                posY += Const.TANK_DIAG;
                break;
            case 6:
                posX -= Const.TANK_H2;
                break;
            case 7:
                posX -= Const.TANK_DIAG;
                posY -= Const.TANK_DIAG;
                break;
        }
        return new Bullet(or, posX, posY);
    }*/


    public static Polygon createPolygon(Coordinate[] coords) {
        LinearRing ring = new LinearRing(new CoordinateArraySequence(coords), GEOMETRY_FACTORY);
        return new Polygon(ring, new LinearRing[0], GEOMETRY_FACTORY);
    }

    public static LineString createLine(Coordinate[] coords) {
        return new LineString(new CoordinateArraySequence(coords), GEOMETRY_FACTORY);
    }

    public static com.vividsolutions.jts.geom.Point createPoint(int x, int y) {
        return new com.vividsolutions.jts.geom.Point(new CoordinateArraySequence(new Coordinate[] {new Coordinate(x, y)}), GEOMETRY_FACTORY);
    }
    
    private boolean collidesBaseWalls(Polygon t, int bx, int by) {
        AffineTransformation tr = AffineTransformation.translationInstance(bx, by);
        LineString lw = (LineString) tr.transform(Const.BASE_LEFT_WALL);
        LineString rw = (LineString) tr.transform(Const.BASE_RIGHT_WALL);
        if (lw.intersects(t)) {return true;}
        if (rw.intersects(t)) {return true;}
        return false;
    }
    
    private void hitTank(boolean first) {
        if (first) {
            t1h -= Const.BULLET_DAMAGE;
            if (t1h <= 0) {
                t1h = 0;
                t1d++;
                exploded = 1;
            }
        } else {
            t2h -= Const.BULLET_DAMAGE;
            if (t2h <= 0) {
                t2h = 0;
                t2d++;
                exploded = 2;
            }
        }
    }

    public int isExploded() {
        return exploded;
    }

    private void recharge() {
        if ((t1x >= b1x) && (t1x < b1x+Const.BASE_WIDTH) && (t1y >= b1y) && (t1y < b1y+Const.BASE_HEIGHT)) {
          if (t1h < Const.MAX_HEALTH) {t1h += Const.HEALTH_INC; if (t1h > Const.MAX_HEALTH) {t1h = Const.MAX_HEALTH;}}
          if (t1e < Const.MAX_ENERGY) {t1e += Const.ENERGY_INC; if (t1e > Const.MAX_ENERGY) {t1e = Const.MAX_ENERGY;}}
        }
        if ((t2x >= b2x) && (t2x < b2x+Const.BASE_WIDTH) && (t2y >= b2y) && (t2y < b2y+Const.BASE_HEIGHT)) {
          if (t2h < Const.MAX_HEALTH) {t2h += Const.HEALTH_INC; if (t2h > Const.MAX_HEALTH) {t2h = Const.MAX_HEALTH;}}
          if (t2e < Const.MAX_ENERGY) {t2e += Const.ENERGY_INC; if (t2e > Const.MAX_ENERGY) {t2e = Const.MAX_ENERGY;}}
        }
    }

    public int getB1x() {
        return b1x;
    }

    public int getB1y() {
        return b1y;
    }

    public int getB2x() {
        return b2x;
    }

    public int getB2y() {
        return b2y;
    }

    public List<Polygon> getStones() {
        return stones;
    }

    public int getT1x() {
        return t1x;
    }

    public int getT1y() {
        return t1y;
    }

    public int getT2x() {
        return t2x;
    }

    public int getT2y() {
        return t2y;
    }
    
}
