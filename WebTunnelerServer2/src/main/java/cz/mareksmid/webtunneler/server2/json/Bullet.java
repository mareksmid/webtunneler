/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2.json;

import cz.mareksmid.webtunneler.server2.Const;

/**
 *
 * @author marek
 */
public class Bullet {

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
            y -= Const.BULLET_SHOOT_INCR_RECT;
            break;
        case 1:
            x += Const.BULLET_SHOOT_INCR_DIAG;
            y -= Const.BULLET_SHOOT_INCR_DIAG;
            break;
        case 2:
            x += Const.BULLET_SHOOT_INCR_RECT;
            break;
        case 3:
            x += Const.BULLET_SHOOT_INCR_DIAG;
            y += Const.BULLET_SHOOT_INCR_DIAG;
            break;
        case 4:
            y += Const.BULLET_SHOOT_INCR_RECT;
            break;
        case 5:
            x -= Const.BULLET_SHOOT_INCR_DIAG;
            y += Const.BULLET_SHOOT_INCR_DIAG;
            break;
        case 6:
            x -= Const.BULLET_SHOOT_INCR_RECT;
            break;
        case 7:
            x -= Const.BULLET_SHOOT_INCR_DIAG;
            y -= Const.BULLET_SHOOT_INCR_DIAG;
            break;
        }
        return (x >= 0) && (y >= 0) && (x < Const.ARENA_WIDTH) && (y < Const.ARENA_HEIGHT);
    }

    @Override
    public String toString() {
        return "B:" + x + "," + y;
    }
    
}
