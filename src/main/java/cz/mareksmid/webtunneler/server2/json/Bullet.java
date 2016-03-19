package cz.mareksmid.webtunneler.server2.json;

/**
 * Mutable!
 */
public class Bullet {

    private final int or;
    private final int id;
    private int x, y;

    
    public Bullet(int id, int or, int x, int y) {
        this.id = id;
        this.or = or;
        this.x = x;
        this.y = y;
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

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "B:" + x + "," + y;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
