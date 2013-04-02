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
    private int b1, b2;
    
    private final boolean[][] dirt = new boolean[Const.DIRT_X_CNT][Const.DIRT_Y_CNT];
    private final Set<Point> dirtUpdateFirst, dirtUpdateSecond;
    private final Set<Bullet> bullets;
    
    private final Timer bulletTimer = new Timer(Const.BULLET_INTERVAL, this);
    
    public Scene(int b1x, int b1y, int b2x, int b2y, List<Polygon> stones) {
        this.b1x = b1x;
        this.b1y = b1y;
        this.b2x = b2x;
        this.b2y = b2y;
        this.stones = stones;
        
        dirtUpdateFirst = Collections.synchronizedSet(new HashSet<Point>());
        dirtUpdateSecond = Collections.synchronizedSet(new HashSet<Point>());
        bullets = Collections.synchronizedSet(new HashSet<Bullet>());
    }
        

    public UpdatePacket updateFirst(PosPacket p) {
        int x = p.getX();
        int y= p.getY();
        or1 = p.getOr();
        b1 = p.getB();
        int[] xy = {x, y};
        checkCollisions(xy, true);
        rx1 = xy[0]; ry1 = xy[1];
        for (int b = 0; b < b1; b++) {
            bullets.add(createBullet(or1, rx1, ry1));
        }
        UpdatePacket up;
        synchronized(dirtUpdateFirst) {
            up = new UpdatePacket(rx1, ry1, or2, rx2, ry2, b2, new HashSet<Point>(dirtUpdateFirst));
            dirtUpdateFirst.clear();
        }
        return up;
    }

    private void checkCollisions(int[] xy, boolean first) {
        if (first) {if (or1 == Const.ORIENTATION_EXPLODED) {return;}}
        else {if (or2 == Const.ORIENTATION_EXPLODED) {return;}}

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

    public UpdatePacket updateSecond(PosPacket p) {
        int x = p.getX();
        int y = p.getY();
        or2 = p.getOr();
        b2 = p.getB();
        int[] xy = {x, y};
        checkCollisions(xy, false);
        rx2 = xy[0]; ry2 = xy[1];
        for (int b = 0; b < b2; b++) {
            bullets.add(createBullet(or2, rx2, ry2));
        }
        UpdatePacket up;
        synchronized(dirtUpdateFirst) {
            up = new UpdatePacket(rx2, ry2, or1, rx1, ry1, b1, new HashSet<Point>(dirtUpdateSecond));
            dirtUpdateSecond.clear();
        }
       return up;
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
        dirtUpdateFirst.clear();
        dirtUpdateSecond.clear();
        bullets.clear();
        bulletTimer.start();
    }
    
    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == bulletTimer) {
            /*int x = (int) (Math.random()*20);
            int y = (int) (Math.random()*20);
            dig(x, y);*/
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
    }
    
    private void dig(int x, int y) {
        dirt[x][y] = false;
        synchronized(dirtUpdateFirst) {
            dirtUpdateFirst.add(new Point(x, y));
        }
        synchronized(dirtUpdateSecond) {
            dirtUpdateSecond.add(new Point(x, y));
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
}
