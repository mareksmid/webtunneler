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

    public static final int ORIENTATION_EXPLODED = 99;

    private int rx, ry;
            
    private int eor;
    private int ex;
    private int ey;
    private int eb;
    
    private Set<Point> dirtRemoved;

    
    public UpdatePacket(int rx, int ry, int eor, int ex, int ey, int eb, Set<Point> dirtRemoved) {
        this.rx = rx;
        this.ry = ry;
        this.eor = eor;
        this.ex = ex;
        this.ey = ey;
        this.eb = eb;
        this.dirtRemoved = dirtRemoved;
    }
    
    public int getRx() {
        return rx;
    }

    public void setRx(int rx) {
        this.rx = rx;
    }

    public int getRy() {
        return ry;
    }

    public void setRy(int ry) {
        this.ry = ry;
    }
    
    
    

    public int getEb() {
        return eb;
    }

    public void setEb(int eb) {
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

    public Set<Point> getDirtRemoved() {
        return dirtRemoved;
    }

    public void setDirtRemoved(Set<Point> dirtRemoved) {
        this.dirtRemoved = dirtRemoved;
    }

    

}
