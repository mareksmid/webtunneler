package cz.mareksmid.webtunneler.server2;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import cz.mareksmid.webtunneler.server2.json.PolygonJS;
import cz.mareksmid.webtunneler.server2.json.ScenePacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SceneGen {
    
    public ScenePacket[] getScenePackets(Scene scene) {
        ScenePacket[] sps = new ScenePacket[2];
        sps[0] = new ScenePacket(scene.getT1x(), scene.getT1y(), scene.getT2x(), scene.getT2y(), scene.getB1x(), scene.getB1y(), scene.getB2x(), scene.getB2y());
        sps[1] = new ScenePacket(scene.getT2x(), scene.getT2y(), scene.getT1x(), scene.getT1y(), scene.getB2x(), scene.getB2y(), scene.getB1x(), scene.getB1y());
        
        Set<PolygonJS> ss = new HashSet<>();
        for (Polygon s : scene.getStones()) {
            ss.add(new PolygonJS(s));
        }
        sps[0].setStones(ss);
        sps[1].setStones(ss);
        
        return sps;
    }
    
    public Scene generateScene() {
        int b1x, b1y, b2x, b2y;
        List<Polygon> stones = new ArrayList<>();

        b1x = (int) (Math.random() * (Const.ARENA_WIDTH-Const.BASE_WIDTH) / Const.DIRT_W) * Const.DIRT_W;
        b1y = (int) (Math.random() * (Const.ARENA_HEIGHT-Const.BASE_HEIGHT) / Const.DIRT_H) * Const.DIRT_H;
        do {
            b2x = (int) (Math.random() * (Const.ARENA_WIDTH-Const.BASE_WIDTH) / Const.DIRT_W) * Const.DIRT_W;
            b2y = (int) (Math.random() * (Const.ARENA_HEIGHT-Const.BASE_HEIGHT) / Const.DIRT_H) * Const.DIRT_H;
        } while ((b1x+Const.BASE_WIDTH >= b2x) && (b2x+Const.BASE_WIDTH >= b1x) &&
                (b1y+Const.BASE_HEIGHT >= b2y) && (b2y+Const.BASE_HEIGHT >= b1y));
        
        for (int i = 0; i < Const.STONE_CNT; i++) {
            Polygon s = null;
            boolean intersects = false;
            do {
                s = generateStone();
                //Rectangle2D b = s.getBounds2D();
                intersects = false;
                for (Polygon os : stones) {
                    if (os.intersects(s)) {
                        intersects = true;
                        break;
                    }
                }
            } while (intersects);
            stones.add(s);
        }

        Scene scene = new Scene(b1x, b1y, b2x, b2y, stones);
        scene.init();
        
        return scene;
    }
    
    
    private Polygon generateStone() {
        int x = (int) (Const.ARENA_WIDTH * Math.random());
        int y = (int) (Const.ARENA_HEIGHT * Math.random());

        List<Coordinate> points = new ArrayList<>();
        for (double ang = 0; ang < Math.PI*2; ang += Math.PI*2/6*Math.random()) {
            double r = Math.random() * 20 + 30;
            points.add(new Coordinate(x  + Math.cos(ang)*r, y + Math.sin(ang)*r));
        }
        points.add(points.get(0));

        return Scene.createPolygon(points.toArray(new Coordinate[points.size()]));
    }
        
}
