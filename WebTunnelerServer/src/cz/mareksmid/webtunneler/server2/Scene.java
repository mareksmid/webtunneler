/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2;

import cz.mareksmid.webtunneler.server2.json.PosPacket;
import cz.mareksmid.webtunneler.server2.json.UpdatePacket;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Timer;

/**
 *
 * @author marek
 */
public class Scene implements ActionListener {
    
    public static final int BULLET_INTERVAL = 500;

    public static final int DIRT_W = 10;
    public static final int DIRT_H = 10;
    public static final int DIRT_X_CNT = SceneGen.ARENA_WIDTH / DIRT_W;
    public static final int DIRT_Y_CNT = SceneGen.ARENA_HEIGHT / DIRT_H;
    
    private int rx1, ry1, rx2, ry2;
            
    private int or1, or2;
    private int b1, b2;
    
    private final boolean[][] dirt = new boolean[DIRT_X_CNT][DIRT_Y_CNT];
    private final Set<Point> dirtUpdateFirst = Collections.synchronizedSet(new HashSet<Point>()), dirtUpdateSecond =  Collections.synchronizedSet(new HashSet<Point>());
    
    private final Timer bulletTimer = new Timer(BULLET_INTERVAL, this);
        

    public UpdatePacket updateFirst(PosPacket p) {
        rx1 = p.getX();
        ry1 = p.getY();
        or1 = p.getOr();
        b1 = p.getB();
        synchronized(dirtUpdateFirst) {
            UpdatePacket up = new UpdatePacket(rx1, ry1, or2, rx2, ry2, b2, new HashSet<Point>(dirtUpdateFirst));
            dirtUpdateFirst.clear();
            return up;
        }
    }
    
    public UpdatePacket updateSecond(PosPacket p) {
        rx2 = p.getX();
        ry2 = p.getY();
        or2 = p.getOr();
        b2 = p.getB();
        synchronized(dirtUpdateFirst) {
            UpdatePacket up = new UpdatePacket(rx2, ry2, or1, rx1, ry1, b1, new HashSet<Point>(dirtUpdateSecond));
            dirtUpdateSecond.clear();
            return up;
        }
    }
    
    public void init() {
        for (int i = 0; i < DIRT_X_CNT; i++) {
            Arrays.fill(dirt[i], true);
        }
        dirtUpdateFirst.clear();
        dirtUpdateSecond.clear();
        bulletTimer.start();
    }
    
    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == bulletTimer) {
            int x = (int) (Math.random()*20);
            int y = (int) (Math.random()*20);
            dirt[x][y] = false;
            dirtUpdateFirst.add(new Point(x, y));
            dirtUpdateSecond.add(new Point(x, y));
        }
    }
    
}
