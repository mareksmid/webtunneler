/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mareksmid.webtunneler.server2.json;

import java.awt.Point;
//import java.awt.Polygon;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

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
        LineString ls = p.getExteriorRing();
        for (int i = 0; i < ls.getNumPoints(); i++) {
            com.vividsolutions.jts.geom.Point pt = ls.getPointN(i);
            points.add(new Point((int) Math.round(pt.getX()), (int) Math.round(pt.getY())));
        }
    }
    
}
