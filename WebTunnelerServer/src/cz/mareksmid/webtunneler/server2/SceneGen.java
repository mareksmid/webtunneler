/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.scene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author marek
 */
public class SceneGen extends JPanel {
    
    public static void main(String args[]) {
        new SceneGen();
    }
    
    private List<Polygon> polygons;
    
    public SceneGen() {
        JFrame frm = new JFrame("WebTunneler scene gen");
        
        polygons = new ArrayList<Polygon>();
        
        polygons.add(generateRandomPolygon());
        polygons.add(generateRandomPolygon());
        polygons.add(generateRandomPolygon());
        
        frm.setSize(500, 400);
        frm.setContentPane(this);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
        
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setPaint(Color.RED);
        for (Polygon p : polygons) {
            g2.draw(p);
        }
    }
    
    
    
    private Polygon generateRandomPolygon() {
        Polygon p = new Polygon();
        int w = 500;
        int h = 400;

        int x = (int) (w * Math.random());
        int y = (int) (h * Math.random());
        
        //p.addPoint(x, y);
        
        for (double ang = 0; ang < Math.PI*2; ang += Math.PI*2/8*Math.random()) {
            double r = Math.random() * 30 + 20;
            
            p.addPoint((int) (x  + Math.cos(ang)*r), (int) (y + Math.sin(ang)*r));
        }
        
        return p;
    }
    
    
    
}
