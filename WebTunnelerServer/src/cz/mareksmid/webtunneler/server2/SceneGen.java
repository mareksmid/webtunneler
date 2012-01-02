/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.PolygonJS;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        
        System.out.println(">"+gen.toJSON());

        JFrame frm = new JFrame("WebTunneler scene gen");
        frm.setSize(w, h);
        frm.setContentPane(gen);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }
    
    private int width, height;
    private List<Polygon> stones;
    
    public SceneGen(int w, int h) {
        width = w;
        height = h;
        stones = new ArrayList<Polygon>();
        
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
    
    public String toJSON() {
        Gson gs = new Gson();
        Map<String,Object> map = new HashMap<String,Object>();
        
        Set<PolygonJS> ss = new HashSet<PolygonJS>();
        map.put("stones", ss);
        for (Polygon s : stones) {
            ss.add(new PolygonJS(s));
        }
        
        return gs.toJson(map);
    }
    
}
