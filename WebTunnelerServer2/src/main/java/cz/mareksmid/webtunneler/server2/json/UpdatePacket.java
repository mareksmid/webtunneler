/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2.json;


import java.awt.Point;
import java.util.Set;

/**
 *
 * @author marek
 */
public class UpdatePacket {

    private int x, y;
    private int h, e;
            
    private int eor;
    private int ex, ey;
    private Set<Bullet> eb;
    
    private Set<Point> drem;

    
    public UpdatePacket(int x, int y, int h, int e, int eor, int ex, int ey, Set<Bullet> eb, Set<Point> drem) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.e = e;
        this.eor = eor;
        this.ex = ex;
        this.ey = ey;
        this.eb = eb;
        this.drem = drem;
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
    
    
    

    public Set<Bullet> getEb() {
        return eb;
    }

    public void setEb(Set<Bullet> eb) {
        this.eb = eb;
    }

    public int getEor() {
        return eor;
    }

    public void setEor(int eor) {
        this.eor = eor;
    }

    public int getEx() {
        return ex;
    }

    public void setEx(int ex) {
        this.ex = ex;
    }

    public int getEy() {
        return ey;
    }

    public void setEy(int ey) {
        this.ey = ey;
    }

    public Set<Point> getDrem() {
        return drem;
    }

    public void setDrem(Set<Point> drem) {
        this.drem = drem;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }


}
