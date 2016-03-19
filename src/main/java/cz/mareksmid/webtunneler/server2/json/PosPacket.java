package cz.mareksmid.webtunneler.server2.json;


import java.util.Set;

public class PosPacket {

    private final int or;
    private final int x;
    private final int y;
    private final Set<Bullet> b;

    public PosPacket(int or, int x, int y, Set<Bullet> b) {
        this.or = or;
        this.x = x;
        this.y = y;
        this.b = b;
    }

    public Set<Bullet> getB() {
        return b;
    }

    public int getOr() {
        return or;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
