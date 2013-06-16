/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2;

import com.vividsolutions.jts.geom.*;
import cz.mareksmid.webtunneler.server2.json.PolygonJS;
import cz.mareksmid.webtunneler.server2.json.ScenePacket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author marek
 */
public class SceneGen extends JPanel {
    

    public static final int STONE_CNT = 20;
    
    public static void main(String args[]) {
        int w = 1000, h = 750;
        
        SceneGen gen = new SceneGen(w, h);
        
        //System.out.println(">"+gen.toJSON());

        JFrame frm = new JFrame("WebTunneler scene gen");
        frm.setSize(w, h);
        frm.setContentPane(gen);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }
    
    private int b1x, b1y, b2x, b2y;
    private int width, height;
    private List<Polygon> stones;
    
    public SceneGen() {
        this(Const.ARENA_WIDTH, Const.ARENA_HEIGHT);
    }
    
    public SceneGen(int w, int h) {
        width = w;
        height = h;
        stones = new ArrayList<Polygon>();

        
        b1x = (int) (Math.random() * (Const.ARENA_WIDTH-Const.BASE_WIDTH) / Const.DIRT_W) * Const.DIRT_W;
        b1y = (int) (Math.random() * (Const.ARENA_HEIGHT-Const.BASE_HEIGHT) / Const.DIRT_H) * Const.DIRT_H;
        do {
            b2x = (int) (Math.random() * (Const.ARENA_WIDTH-Const.BASE_WIDTH) / Const.DIRT_W) * Const.DIRT_W;
            b2y = (int) (Math.random() * (Const.ARENA_HEIGHT-Const.BASE_HEIGHT) / Const.DIRT_H) * Const.DIRT_H;
        } while ((b1x+Const.BASE_WIDTH >= b2x) && (b2x+Const.BASE_WIDTH >= b1x) &&
                (b1y+Const.BASE_HEIGHT >= b2y) && (b2y+Const.BASE_HEIGHT >= b1y));
        
        for (int i = 0; i < STONE_CNT; i++) {
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
    }

    /*@Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setPaint(Color.RED);
        for (Polygon p : stones) {
            g2.draw(p);
        }
    }*/

    
    public ScenePacket[] generatePackets() {
        ScenePacket[] scene = new ScenePacket[2];
        scene[0] = new ScenePacket(b1x, b1y, b2x, b2y);
        scene[1] = new ScenePacket(b2x, b2y, b1x, b1y);
        
        Set<PolygonJS> ss = new HashSet<PolygonJS>();
        for (Polygon s : stones) {
            ss.add(new PolygonJS(s));
        }
        scene[0].setStones(ss);
        scene[1].setStones(ss);
        
        return scene;
    }
    
    public Scene generateScene() {
        return new Scene(b1x, b1y, b2x, b2y, stones);
    }
    
    
    private Polygon generateStone() {
        int x = (int) (width * Math.random());
        int y = (int) (height * Math.random());

        List<Coordinate> points = new ArrayList<Coordinate>();
        for (double ang = 0; ang < Math.PI*2; ang += Math.PI*2/6*Math.random()) {
            double r = Math.random() * 20 + 30;
            points.add(new Coordinate(x  + Math.cos(ang)*r, y + Math.sin(ang)*r));
        }
        points.add(points.get(0));

        return Scene.createPolygon(points.toArray(new Coordinate[points.size()]));
    }
        
}
