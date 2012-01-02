/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.*;
import java.util.HashMap;
import java.util.Map;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketServerListener;
import org.jwebsocket.kit.WebSocketServerEvent;

/**
 *
 * @author marek
 */
public class WSListener implements WebSocketServerListener {

    public static final String INIT_NEW = "NEW";
    public static final String INIT_JOIN = "JOIN";

    Map<WebSocketConnector, WSWorker> workers;
    Map<String, WSWorker> workersById;

    public WSListener() {
        workers = new HashMap<WebSocketConnector, WSWorker>();
        workersById = new HashMap<String, WSWorker>();
    }

    public void processOpened(WebSocketServerEvent wsse) {
        System.out.println("opened: "+wsse);
        //System.out.println("session:"+wsse.getSession());
        //System.out.println("connector:"+wsse.getConnector());
        //System.out.println("username:"+wsse.getServer().getUsername(wsse.getConnector()));

        //WebSocketConnector c = wsse.getConnector();

        //RequestHeader h = c.getHeader();
        //System.out.println(">"+h.getArgs());
        //System.out.println(">"+h);
        //System.out.println(">"+h.getSubProtocol());
        //System.out.println("#"+h.get("path"));
        
        //wsse.sendPacket(new RawPacket("aaaaaa"));
        
        //wsse.getConnector().setSubprot("text");
        
        //System.out.println(">"+wsse.getConnector().getStatus());
        //System.out.println(">"+wsse.getConnector().getSubprot());
        //System.out.println(">"+wsse.getConnector().getVersion());
        
        //System.out.println(">>>"+wsse.getConnector());
        //System.out.println(">>>"+wsse.getConnector().getClass());
        //System.out.println(">>>"+wsse.getConnector().getHeader());
        //System.out.println(">>>"+wsse.getConnector().getHeader().getClass());
        //RequestHeader lHeader = wsse.getConnector().getHeader();
        //System.out.println(">>>"+lHeader.getFormat());
        //String lFormat = (lHeader != null ? lHeader.getFormat() : null);
        //System.out.println("$$$"+(lFormat != null && JWebSocketCommonConstants.WS_FORMAT_TEXT.equals(lFormat)));
    }

    public void processPacket(WebSocketServerEvent wsse, WebSocketPacket wsp) {
        //System.out.println("packet: "+wsp.getString());
        String s = wsp.getString();
        WebSocketConnector c = wsse.getConnector();
        WSWorker w = workers.get(c);

        Gson g = new Gson();
        
        if (w == null) {
            InitPacket i = g.fromJson(s, InitPacket.class);
            //String[] ss = s.split(":");
            /*if (ss.length != 2) {
                System.err.println("Invalid init packet: "+s);
                return;
            }*/

            //String cmd = ss[0];
            //String id = ss[1];
            if (i.getCmd().equals(INIT_NEW)) {
                System.out.println("new worker for "+i.getId());
                w = new WSWorker(c, i.getId());
                workersById.put(i.getId(), w);

            } else if (i.getCmd().equals(INIT_JOIN)) {
                w = workersById.get(i.getId());
                if (w == null) {
                    System.err.println("Worker for second does not exist: "+i.getId());
                    return;
                }
                System.out.println("joined worker for "+i.getId());
                w.setSecond(c);

            } else {
                System.err.println("Unknown init packet: "+s);
                return;
            }

            workers.put(c, w);
            return;
        }
        
        PosPacket p = g.fromJson(s, PosPacket.class);
        w.processPacket(p, c);
    }

    public void processClosed(WebSocketServerEvent wsse) {
        System.out.println("closed: "+wsse);
    }


}
