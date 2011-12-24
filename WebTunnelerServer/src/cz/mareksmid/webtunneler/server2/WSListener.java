/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import java.util.HashMap;
import java.util.Map;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketServerListener;
import org.jwebsocket.kit.RequestHeader;
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

        WebSocketConnector c = wsse.getConnector();

        RequestHeader h = c.getHeader();
        //System.out.println(">"+h.getArgs());
        //System.out.println(">"+h);
        //System.out.println(">"+h.getSubProtocol(null));
        //System.out.println("#"+h.get("path"));
        


    }

    public void processPacket(WebSocketServerEvent wsse, WebSocketPacket wsp) {
        //System.out.println("packet: "+wsp.getString());
        String s = wsp.getString();
        WebSocketConnector c = wsse.getConnector();
        WSWorker w = workers.get(c);

        if (w == null) {
            String[] ss = s.split(":");
            if (ss.length != 2) {
                System.err.println("Invalid init packet: "+s);
                return;
            }

            String cmd = ss[0];
            String id = ss[1];
            if (cmd.equals(INIT_NEW)) {
                System.out.println("new worker for "+id);
                w = new WSWorker(c, id);
                workersById.put(id, w);

            } else if (cmd.equals(INIT_JOIN)) {
                w = workersById.get(id);
                if (w == null) {
                    System.err.println("Worker for second does not exist: "+id);
                    return;
                }
                System.out.println("joined worker for "+id);
                w.setSecond(c);

            } else {
                System.err.println("Unknown init packet: "+s);
                return;
            }

            workers.put(c, w);
            return;
        }
        
        w.processPacket(s, c);
    }

    public void processClosed(WebSocketServerEvent wsse) {
        System.out.println("closed: "+wsse);
    }


}
