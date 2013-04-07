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
import cz.mareksmid.webtunneler.server2.model.Bullet;

//import java.awt.*;
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

    /*private static Polygon rotate(Polygon poly, double ang) {
        Matrix m = new Matrix(ang);
        int[] xp = new int[poly.npoints];
        int[] yp = new int[poly.npoints];
        for (int i = 0; i < poly.npoints; i++) {
            double[] v = m.multiply(new double[] {poly.xpoints[i], poly.ypoints[i]});
            xp[i] = (int) Math.round(v[0]);
            yp[i] = (int) Math.round(v[1]);
        }
        return new Polygon(xp, yp, xp.length);
    }*/


    private int rx1, ry1, rx2, ry2;

    private int b1x, b1y, b2x, b2y;
    private List<Polygon> stones;
    
    private int or1, or2;
    //private int b1, b2;
    
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
        int y= p.getY();
        if (first) or1 = p.getOr();
        else or2 = p.getOr();
        //b1 = p.getB();
        //b2 = p.getB();
        int[] xy = {x, y};
        checkCollisions(xy, first);
        if (first) {rx1 = xy[0]; ry1 = xy[1];}
        else {rx2 = xy[0]; ry2 = xy[1];}
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
            up = new UpdatePacket(rx1, ry1, or2, rx2, ry2, bulletsUpdate1, new HashSet<Point>(dirtUpdate1));
            dirtUpdate1.clear();
            bulletsUpdate1.clear();
        } else synchronized(dirtUpdate2) {
            up = new UpdatePacket(rx2, ry2, or1, rx1, ry1, bulletsUpdate2, new HashSet<Point>(dirtUpdate2));
            dirtUpdate2.clear();
            bulletsUpdate2.clear();
        }
        return up;
    }

    private void checkCollisions(int[] xy, boolean first) {
        //if (first) {if (or1 == Const.ORIENTATION_EXPLODED) {return;}}
        //else {if (or2 == Const.ORIENTATION_EXPLODED) {return;}}

        boolean c = false;
        Polygon t, et;
        if (first) {t = Const.TANK_POLYS[or1]; et = Const.TANK_POLYS[or2];}
        else {t = Const.TANK_POLYS[or2]; et = Const.TANK_POLYS[or1];}
        //t = clone(t); et = clone(et);
        AffineTransformation tr = AffineTransformation.translationInstance(xy[0], xy[1]);
        AffineTransformation etr;
        //t.translate(xy[0], xy[1]);
        t = (Polygon) tr.transform(t);
        /*if (first) {et.translate(rx2, ry2);}
        else {et.translate(rx1, ry1);}*/
        if (first) {etr = AffineTransformation.translationInstance(rx2, ry2);}
        else {etr = AffineTransformation.translationInstance(rx1, rx2);}
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
        //private int b1x, b1y, b2x, b2y;

        if (c) {
            if (first) {
                xy[0] = rx1; xy[1] = ry1;
            } else {
                xy[0] = rx2; xy[1] = ry2;
            }
        }

    }

    /*private Polygon clone(Polygon p) {
        return new Polygon(p.xpoints.clone(), p.ypoints.clone(), p.npoints);

    }*/

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
        bulletTimer.start();
    }
    
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
            if (dirt[x][y]) {
                dig(x, y);
                iter.remove();
            } else {
                if (!b.move()) {iter.remove();}
            }
        }
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
    
    private Bullet createBullet(int or, int x, int y) {
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
    }


    public static Polygon createPolygon(Coordinate[] coords) {
        LinearRing ring = new LinearRing(new CoordinateArraySequence(coords), GEOMETRY_FACTORY);
        return new Polygon(ring, new LinearRing[0], GEOMETRY_FACTORY);
    }

    public static LineString createLine(Coordinate[] coords) {
        return new LineString(new CoordinateArraySequence(coords), GEOMETRY_FACTORY);

    }

    private boolean collidesBaseWalls(Polygon t, int bx, int by) {
        AffineTransformation tr = AffineTransformation.translationInstance(bx, by);
        LineString lw = (LineString) tr.transform(Const.BASE_LEFT_WALL);
        LineString rw = (LineString) tr.transform(Const.BASE_RIGHT_WALL);
        if (lw.intersects(t)) {return true;}
        if (rw.intersects(t)) {return true;}
        return false;
    }

    public boolean isExploded() {
        return false;
    }
}
