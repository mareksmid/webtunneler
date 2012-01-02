/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.InitResponsePacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.RawPacket;

/**
 *
 * @author marek
 */
class WSWorker {
    public static final int ARENA_WIDTH = 1600;
    public static final int ARENA_HEIGHT = 1200;
    public static final int BASE_WIDTH = 120;
    public static final int BASE_HEIGHT = 120;

    private boolean assigned = false;
    private WebSocketConnector first, second = null;
    private String id;

    public WSWorker(WebSocketConnector c, String id) {
        first = c;
        this.id = id;
    }

    public void processPacket(PosPacket pp, WebSocketConnector c) {
        if (!assigned) {return;}

        WebSocketPacket p = new RawPacket(new Gson().toJson(pp));

        if (c.equals(first)) {
            second.sendPacket(p);
        } else {
            first.sendPacket(p);
        }
        //System.out.println(">"+s);

        //if (s.equals("EXPL")) {
        if (pp.getOr() == PosPacket.ORIENTATION_EXPLODED) {
            init();
        }
    }

    public void setSecond(WebSocketConnector c) {
        if (assigned) {throw new RuntimeException("Already assigned");}
        second = c;
        assigned = true;
        init();
    }

    public boolean isAssigned() {
        return assigned;
    }

    private void init() {
        int b1x, b1y, b2x, b2y;
        b1x = (int) (Math.random() * (ARENA_WIDTH-BASE_WIDTH));
        b1y = (int) (Math.random() * (ARENA_HEIGHT-BASE_HEIGHT));
        do {
            b2x = (int) (Math.random() * (ARENA_WIDTH-BASE_WIDTH));
            b2y = (int) (Math.random() * (ARENA_HEIGHT-BASE_HEIGHT));
        } while ((b1x+BASE_WIDTH >= b2x) && (b2x+BASE_WIDTH >= b1x) &&
                (b1y+BASE_HEIGHT >= b2y) && (b2y+BASE_HEIGHT >= b1y));


        InitResponsePacket rp1 = new InitResponsePacket(b1x, b1y, b2x, b2y);
        InitResponsePacket rp2 = new InitResponsePacket(b2x, b2y, b1x, b1y);
        Gson g = new Gson();
        WebSocketPacket p1 = new RawPacket(g.toJson(rp1));
        WebSocketPacket p2 = new RawPacket(g.toJson(rp2));
        first.sendPacket(p1);
        second.sendPacket(p2);
    }

}
