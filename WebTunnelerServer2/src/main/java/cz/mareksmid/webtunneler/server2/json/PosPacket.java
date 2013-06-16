/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2.json;


import java.util.Set;

/**
 *
 * @author marek
 */
public class PosPacket {

    private int or;
    private int x;
    private int y;
    private Set<Bullet> b;

    public Set<Bullet> getB() {
        return b;
    }

    public void setB(Set<Bullet> b) {
        this.b = b;
    }

    public int getOr() {
        return or;
    }

    public void setOr(int or) {
        this.or = or;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    
}
