package cz.mareksmid.webtunneler.server2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;

public class Const {

    public static final int BULLET_INTERVAL = 40;

    public static final int ARENA_WIDTH = 1600;
    public static final int ARENA_HEIGHT = 1200;
    public static final int BASE_WIDTH = 120;
    public static final int BASE_ENTRANCE = 60;
    public static final int BASE_HEIGHT = 120;

    public static final int TANK_W2 = 16;
    public static final int TANK_H2 = 21;
    public static final int TANK_R = 21;
    public static final int TANK_DIAG = (int) Math.round(Math.sqrt(2)/2*TANK_H2);

    public static final int BULLET_INCR_RECT = 10;
    public static final int BULLET_INCR_DIAG = 7;
    public static final int BULLET_SHOOT_INCR_RECT = 20;
    public static final int BULLET_SHOOT_INCR_DIAG = 14;

    public static final int DIRT_W = 20;
    public static final int DIRT_H = 20;
    public static final int DIRT_X_CNT = ARENA_WIDTH / DIRT_W;
    public static final int DIRT_Y_CNT = ARENA_HEIGHT / DIRT_H;
    
    
    public static final int MAX_ENERGY = 100;
    public static final int BULLET_ENERGY = 1;
    public static final int ENERGY_INC = 2;

    public static final int MAX_HEALTH = 100;
    public static final int BULLET_DAMAGE = 5;
    public static final int HEALTH_INC = 1;

    public static final int STONE_CNT = 20;


    public static final Polygon TANK_POLY = Scene.createPolygon(new Coordinate[]{
            new Coordinate(+TANK_W2, -TANK_H2),
            new Coordinate(+TANK_W2, +TANK_H2),
            new Coordinate(-TANK_W2, +TANK_H2),
            new Coordinate(-TANK_W2, -TANK_H2),
            new Coordinate(+TANK_W2, -TANK_H2)
    });

    public static final LineString BASE_LEFT_WALL = Scene.createLine(new Coordinate[]{
            new Coordinate((BASE_WIDTH - BASE_ENTRANCE) / 2, 0),
            new Coordinate(0, 0),
            new Coordinate(0, BASE_HEIGHT),
            new Coordinate((BASE_WIDTH - BASE_ENTRANCE) / 2, BASE_HEIGHT),
    });
    public static final LineString BASE_RIGHT_WALL = Scene.createLine(new Coordinate[]{
            new Coordinate(BASE_WIDTH - (BASE_WIDTH - BASE_ENTRANCE) / 2, 0),
            new Coordinate(BASE_WIDTH, 0),
            new Coordinate(BASE_WIDTH, BASE_HEIGHT),
            new Coordinate(BASE_WIDTH - (BASE_WIDTH - BASE_ENTRANCE) / 2, BASE_HEIGHT),
    });

    public static final Polygon[] TANK_POLYS = new Polygon[8];

    static {
        for (int i = 0; i < 8; i++) {
            double ang = Math.PI*2 * i / 8;
            AffineTransformation tx = AffineTransformation.rotationInstance(-ang);
            TANK_POLYS[i] = (Polygon) tx.transform(TANK_POLY);
        }
    }

}
