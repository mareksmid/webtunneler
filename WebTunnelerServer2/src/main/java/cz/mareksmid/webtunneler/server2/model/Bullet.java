/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2.model;

import cz.mareksmid.webtunneler.server2.Const;
import cz.mareksmid.webtunneler.server2.Scene;

/**
 *
 * @author marek
 */
public class Bullet {

    public static final int INCR_RECT = 10;
    public static final int INCR_DIAG = 7;
    public static final int SHOOT_INCR_RECT = 20;
    public static final int SHOOT_INCR_DIAG = 14;

    
    public int or;
    public int x, y;
    
    
    public Bullet(int or, int x, int y) {
        this.or = or;
        this.x = x;
        this.y = y;
    }
    
    
    public boolean move() {
        switch (or) {
        case 0:
            y -= SHOOT_INCR_RECT;
            break;
        case 1:
            x += SHOOT_INCR_DIAG;
            y -= SHOOT_INCR_DIAG;
            break;
        case 2:
            x += SHOOT_INCR_RECT;
            break;
        case 3:
            x += SHOOT_INCR_DIAG;
            y += SHOOT_INCR_DIAG;
            break;
        case 4:
            y += SHOOT_INCR_RECT;
            break;
        case 5:
            x -= SHOOT_INCR_DIAG;
            y += SHOOT_INCR_DIAG;
            break;
        case 6:
            x -= SHOOT_INCR_RECT;
            break;
        case 7:
            x -= SHOOT_INCR_DIAG;
            y -= SHOOT_INCR_DIAG;
            break;
        }
        return (x >= 0) && (y >= 0) && (x < Const.ARENA_WIDTH) && (y < Const.ARENA_HEIGHT);
    }
    
}
