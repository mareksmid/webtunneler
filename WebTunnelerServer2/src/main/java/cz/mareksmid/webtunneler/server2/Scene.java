/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author marek
 */
public class Scene implements ActionListener {

    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();


    private int t1x, t1y, t2x, t2y;
    private int or1, or2;
    private int t1h, t2h;
    private int t1e, t2e;
    
    private int b1x, b1y, b2x, b2y;
    private List<Polygon> stones;
    
    
    private final boolean[][] dirt = new boolean[Const.DIRT_X_CNT][Const.DIRT_Y_CNT];
    private final Set<Point> dirtUpdate1, dirtUpdate2;
    private final Set<Bullet> bullets1, bullets2;
    private final Set<Bullet> bulletsUpdate1, bulletsUpdate2;
    
    private final Timer bulletTimer = new Timer(Const.BULLET_INTERVAL, this);
    
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
    }
        

    public UpdatePacket update(PosPacket p, boolean first) {
        int x = p.getX();
        int y = p.getY();
        if (first) or1 = p.getOr();
        else or2 = p.getOr();
        if (!checkCollisions(x, y, first)) {
            if (first) {
                t1x = x; t1y = y;
            } else {
                t2x = x; t2y = y;
            }
        }
        if (first) {
            bullets1.addAll(p.getB());
            bulletsUpdate2.addAll(p.getB());
        } else {
            bullets2.addAll(p.getB());
            bulletsUpdate1.addAll(p.getB());
        }
        /*for (int b = 0; b < b1; b++) {
            bullets.add(createBullet(or1, rx1, ry1));
        }
        for (int b = 0; b < b2; b++) {
            bullets.add(createBullet(or2, rx2, ry2));
        }*/
        UpdatePacket up;
        if (first) synchronized(dirtUpdate1) {
            up = new UpdatePacket(t1x, t1y, or2, t2x, t2y, bulletsUpdate1, new HashSet<Point>(dirtUpdate1));
            dirtUpdate1.clear();
            bulletsUpdate1.clear();
        } else synchronized(dirtUpdate2) {
            up = new UpdatePacket(t2x, t2y, or1, t1x, t1y, bulletsUpdate2, new HashSet<Point>(dirtUpdate2));
            dirtUpdate2.clear();
            bulletsUpdate2.clear();
        }
        return up;
    }

    private boolean checkCollisions(int x, int y, boolean first) {
        boolean c = false;
        Polygon t, et;
        if (first) {t = Const.TANK_POLYS[or1]; et = Const.TANK_POLYS[or2];}
        else {t = Const.TANK_POLYS[or2]; et = Const.TANK_POLYS[or1];}
        AffineTransformation tr = AffineTransformation.translationInstance(x, y);
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
        bullets1.clear();
        bullets2.clear();
        t1e = Const.MAX_ENERGY;
        t2e = Const.MAX_ENERGY;
        t1h = Const.MAX_HEALTH;
        t2h = Const.MAX_HEALTH;
        bulletTimer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == bulletTimer) {
            processBullets(bullets1);
            processBullets(bullets2);
        }
    }

    private void processBullets(Set<Bullet> bullets) {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            int x = b.x / Const.DIRT_W;
            int y = b.y / Const.DIRT_H;

            if (collidesTank(true, createPoint(b.x, b.y))) {
                hitTank(true);
                iter.remove();
            } else if (collidesTank(false, createPoint(b.x, b.y))) {
                hitTank(false);
                iter.remove();
            } else if (dirt[x][y]) {
                dig(x, y);
                iter.remove();
            } else {
                if (!b.move()) {iter.remove();}
            }
        }
    }
    
    private boolean collidesTank(boolean first, Geometry g) {
        Polygon t = Const.TANK_POLYS[first?or1:or2];
        AffineTransformation tr = AffineTransformation.translationInstance(first?t1x:t2x, first?t1y:t2y);
        t = (Polygon) tr.transform(t);
        return t.intersects(g);
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
            }
        } else {
            t2h -= Const.BULLET_DAMAGE;
            if (t2h <= 0) {
                t2h = 0;
            }
        }
    }

    public boolean isExploded() {
        return false;
    }
}
