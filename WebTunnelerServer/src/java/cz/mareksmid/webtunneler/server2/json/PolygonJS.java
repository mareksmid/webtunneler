/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2.json;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marek
 */
public class PolygonJS {

    private List<Point> points;
    
    public PolygonJS(Polygon p) {
        points = new ArrayList<Point>();
        for (int i = 0; i < p.npoints; i++) {
            points.add(new Point(p.xpoints[i], p.ypoints[i]));
        }
    }
    
}
