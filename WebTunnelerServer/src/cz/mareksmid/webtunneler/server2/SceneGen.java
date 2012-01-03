/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2;

import cz.mareksmid.webtunneler.server2.json.PolygonJS;
import cz.mareksmid.webtunneler.server2.json.ScenePacket;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
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
    
    public static final int ARENA_WIDTH = 1600;
    public static final int ARENA_HEIGHT = 1200;
    public static final int BASE_WIDTH = 120;
    public static final int BASE_HEIGHT = 120;

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
        this(ARENA_WIDTH, ARENA_HEIGHT);
    }
    
    public SceneGen(int w, int h) {
        width = w;
        height = h;
        stones = new ArrayList<Polygon>();

        
        b1x = (int) (Math.random() * (ARENA_WIDTH-BASE_WIDTH));
        b1y = (int) (Math.random() * (ARENA_HEIGHT-BASE_HEIGHT));
        do {
            b2x = (int) (Math.random() * (ARENA_WIDTH-BASE_WIDTH));
            b2y = (int) (Math.random() * (ARENA_HEIGHT-BASE_HEIGHT));
        } while ((b1x+BASE_WIDTH >= b2x) && (b2x+BASE_WIDTH >= b1x) &&
                (b1y+BASE_HEIGHT >= b2y) && (b2y+BASE_HEIGHT >= b1y));
        
        for (int i = 0; i < STONE_CNT; i++) {
            Polygon s = null;
            boolean intersects = false;
            do {
                s = generateStone();
                Rectangle2D b = s.getBounds2D();
                intersects = false;
                for (Polygon os : stones) {
                    if (os.intersects(b)) {
                        intersects = true;
                        break;
                    }
                }
            } while (intersects);
            stones.add(s);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setPaint(Color.RED);
        for (Polygon p : stones) {
            g2.draw(p);
        }
    }

    
    public ScenePacket[] generate() {
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
    
    
    private Polygon generateStone() {
        Polygon p = new Polygon();
        int x = (int) (width * Math.random());
        int y = (int) (height * Math.random());
        
        for (double ang = 0; ang < Math.PI*2; ang += Math.PI*2/6*Math.random()) {
            double r = Math.random() * 20 + 30;
            p.addPoint((int) (x  + Math.cos(ang)*r), (int) (y + Math.sin(ang)*r));
        }
        
        return p;
    }
        
}
