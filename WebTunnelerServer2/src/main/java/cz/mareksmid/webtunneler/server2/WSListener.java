/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import org.glassfish.websocket.platform.WebSocketWrapper;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.*;
import javax.websocket.server.DefaultServerConfiguration;
import javax.websocket.server.WebSocketEndpoint;

/**
 *
 * @author marek
 */
@WebSocketEndpoint(value="/ws", configuration = DefaultServerConfiguration.class)
@org.glassfish.websocket.api.annotations.WebSocket(path = "/ws")
public class WSListener {

    public static final String INIT_NEW = "NEW";
    public static final String INIT_JOIN = "JOIN";

    Map<String,WSWorker> workers;
    Map<String, WSWorker> workersById;

    public WSListener() {
        workers = new HashMap<String, WSWorker>();
        workersById = new HashMap<String, WSWorker>();
        System.out.println("init");
    }

    @WebSocketOpen
    public void processOpened(Session session, EndpointConfiguration cfg) {
        System.out.println("opened: "+session+" - "+cfg);
    }

    @WebSocketMessage
    @org.glassfish.websocket.api.annotations.WebSocketMessage
    public void processPacket(String message, WebSocketWrapper wsw) { //, Session session) {
        System.out.println("packet: "+message+" - "+wsw); //+" - "+session);


        //String s = wsp.getString();
        String s = message;
        //WebSocketConnector c = wsse.getConnector();
        //WSWorker w = workers.get(c);
        WSWorker w = workers.get(""+wsw.getConversation().getConversationID());

        Gson g = new Gson();
        
        /*if (w == null) {
            InitPacket i = g.fromJson(s, InitPacket.class);
            //String[] ss = s.split(":");

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
        w.processPacket(p, c);*/
    }

    @WebSocketClose
    public void processClosed(Session session) {
        System.out.println("closed: "+session);
    }


    @WebSocketError
    public void processError(Session session, Throwable error) {
        System.out.println("error: "+error+" - "+session);
    }

}
