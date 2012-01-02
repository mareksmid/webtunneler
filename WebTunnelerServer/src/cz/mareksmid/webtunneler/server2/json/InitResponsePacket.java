/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2.json;

/**
 *
 * @author marek
 */
public class InitResponsePacket {
    
    public InitResponsePacket(int bx, int by, int ebx, int eby) {
        this.bx = bx;
        this.by = by;
        this.ebx = ebx;
        this.eby = eby;
    }
    
    private String cmd = "INIT";
    private int bx, by;
    private int ebx, eby;

    public int getBx() {
        return bx;
    }

    public void setBx(int bx) {
        this.bx = bx;
    }

    public int getBy() {
        return by;
    }

    public void setBy(int by) {
        this.by = by;
    }

    public int getEbx() {
        return ebx;
    }

    public void setEbx(int ebx) {
        this.ebx = ebx;
    }

    public int getEby() {
        return eby;
    }

    public void setEby(int eby) {
        this.eby = eby;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
    
    
}
