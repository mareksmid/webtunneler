/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2;

import cz.mareksmid.webtunneler.server2.json.PosPacket;
import cz.mareksmid.webtunneler.server2.json.UpdatePacket;
import cz.mareksmid.webtunneler.server2.model.Bullet;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;

/**
 *
 * @author marek
 */
public class Scene implements ActionListener {
    
    public static final int BULLET_INTERVAL = 40;

    public static final int ARENA_WIDTH = 1600;
    public static final int ARENA_HEIGHT = 1200;
    public static final int BASE_WIDTH = 120;
    public static final int BASE_HEIGHT = 120;

    public static final int TANK_W2 = 16;
    public static final int TANK_H2 = 21;
    public static final int TANK_R = 21;
    public static final int TANK_DIAG = (int) Math.round(Math.sqrt(2)/2*TANK_H2);
    
    public static final int DIRT_W = 10;
    public static final int DIRT_H = 10;
    public static final int DIRT_X_CNT = ARENA_WIDTH / DIRT_W;
    public static final int DIRT_Y_CNT = ARENA_HEIGHT / DIRT_H;
    
    private int rx1, ry1, rx2, ry2;

    private int b1x, b1y, b2x, b2y;
    private List<Polygon> stones;
    
    private int or1, or2;
    private int b1, b2;
    
    private final boolean[][] dirt = new boolean[DIRT_X_CNT][DIRT_Y_CNT];
    private final Set<Point> dirtUpdateFirst, dirtUpdateSecond;
    private final Set<Bullet> bullets;
    
    private final Timer bulletTimer = new Timer(BULLET_INTERVAL, this);
    
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
        rx1 = p.getX();
        ry1 = p.getY();
        or1 = p.getOr();
        b1 = p.getB();
        int x = rx1, y = ry1;
        for (int b = 0; b < b1; b++) {
            bullets.add(createBullet(or1, x, y));
        }
        UpdatePacket up;
        synchronized(dirtUpdateFirst) {
            up = new UpdatePacket(x, y, or2, rx2, ry2, b2, new HashSet<Point>(dirtUpdateFirst));
            dirtUpdateFirst.clear();
        }
        return up;
    }
    
    public UpdatePacket updateSecond(PosPacket p) {
        rx2 = p.getX();
        ry2 = p.getY();
        or2 = p.getOr();
        b2 = p.getB();
        int x = rx2, y = ry2;
        for (int b = 0; b < b2; b++) {
            bullets.add(createBullet(or2, x, y));
        }
        UpdatePacket up;
        synchronized(dirtUpdateFirst) {
            up = new UpdatePacket(x, y, or1, rx1, ry1, b1, new HashSet<Point>(dirtUpdateSecond));
            dirtUpdateSecond.clear();
        }
       return up;
    }
    
    public void init() {
        for (int i = 0; i < DIRT_X_CNT; i++) {
            Arrays.fill(dirt[i], true);
            if ((i >= b1x/DIRT_W) && (i < (b1x+BASE_WIDTH)/DIRT_W)) {
               Arrays.fill(dirt[i], b1y/DIRT_H, (b1y+BASE_HEIGHT)/DIRT_H, false); 
            }
            if ((i >= b2x/DIRT_W) && (i < (b2x+BASE_WIDTH)/DIRT_W)) {
               Arrays.fill(dirt[i], b2y/DIRT_H, (b2y+BASE_HEIGHT)/DIRT_H, false); 
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
                int x = b.x / DIRT_W;
                int y = b.y / DIRT_H;
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
                posY -= TANK_H2;
                break;
            case 1:
                posX += TANK_DIAG;
                posY -= TANK_DIAG;
                break;
            case 2:
                posX += TANK_H2;
                break;
            case 3:
                posX += TANK_DIAG;
                posY += TANK_DIAG;
                break;
            case 4:
                posY += TANK_H2;
                break;
            case 5:
                posX -= TANK_DIAG;
                posY += TANK_DIAG;
                break;
            case 6:
                posX -= TANK_H2;
                break;
            case 7:
                posX -= TANK_DIAG;
                posY -= TANK_DIAG;
                break;
        }
        return new Bullet(or, posX, posY);
    }
    
}
