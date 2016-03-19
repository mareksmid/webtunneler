package cz.mareksmid.webtunneler.server2.json;


import java.awt.Point;
import java.util.Set;

public class UpdatePacket {

    private final int x, y;
    private final int h, e;
            
    private final int eor;
    private final int ex, ey;
    private final Set<Bullet> eb;
    
    private final Set<Integer> brem;
    private final Set<Integer> ebrem;
    private final Set<Point> drem;

    public UpdatePacket(int x, int y, int h, int e, int eor, int ex, int ey, Set<Bullet> eb, Set<Point> drem, Set<Integer> brem, Set<Integer> ebrem) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.e = e;
        this.eor = eor;
        this.ex = ex;
        this.ey = ey;
        this.eb = eb;
        this.drem = drem;
        this.brem = brem;
        this.ebrem = ebrem;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Set<Bullet> getEb() {
        return eb;
    }

    public int getEor() {
        return eor;
    }

    public int getEx() {
        return ex;
    }

    public int getEy() {
        return ey;
    }

    public Set<Point> getDrem() {
        return drem;
    }

    public int getH() {
        return h;
    }

    public int getE() {
        return e;
    }

    public Set<Integer> getBrem() {
        return brem;
    }

    public Set<Integer> getEbrem() {
        return ebrem;
    }

}
