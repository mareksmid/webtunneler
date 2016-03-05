package cz.mareksmid.webtunneler.server2.json;

public class Bullet {

    public int or;
    public int x, y;
    public int id;
    
    
    public Bullet(int id, int or, int x, int y) {
        this.id = id;
        this.or = or;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "B:" + x + "," + y;
    }
    
}
