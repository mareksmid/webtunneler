package cz.mareksmid.webtunneler.server2.json;

import java.util.Set;

public class ScenePacket {

    private static final String cmd = "SCENE";

    private final int tx, ty;
    private final int etx, ety;
    private final int bx, by;
    private final int ebx, eby;
    
    private final Set<PolygonJS> stones;

    
    public ScenePacket(int tx, int ty, int etx, int ety, int bx, int by, int ebx, int eby, Set<PolygonJS> stones) {
        this.tx = tx;
        this.ty = ty;
        this.etx = etx;
        this.ety = ety;
        this.bx = bx;
        this.by = by;
        this.ebx = ebx;
        this.eby = eby;
        this.stones = stones;
    }
    
    public int getBx() {
        return bx;
    }

    public int getBy() {
        return by;
    }

    public int getEbx() {
        return ebx;
    }

    public int getEby() {
        return eby;
    }

    public String getCmd() {
        return cmd;
    }

    public Set<PolygonJS> getStones() {
        return stones;
    }

    public int getTx() {
        return tx;
    }

    public int getTy() {
        return ty;
    }

    public int getEtx() {
        return etx;
    }

    public int getEty() {
        return ety;
    }

}
