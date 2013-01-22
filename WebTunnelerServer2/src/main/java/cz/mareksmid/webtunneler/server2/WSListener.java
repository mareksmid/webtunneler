/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import org.glassfish.websocket.api.annotations.*;
import org.glassfish.websocket.platform.WebSocketWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.*;
import javax.websocket.WebSocketClose;
import javax.websocket.WebSocketError;
import javax.websocket.WebSocketMessage;
import javax.websocket.WebSocketOpen;
import javax.websocket.server.DefaultServerConfiguration;
import javax.websocket.server.WebSocketEndpoint;

/**
 *
 * @author marek
 */
@WebSocketEndpoint(value="/owts", configuration = DefaultServerConfiguration.class)
@WebSocket(path = "/wts")
public class WSListener {

    public static final String INIT_NEW = "NEW";
    public static final String INIT_JOIN = "JOIN";

    Map<Long,WSWorker> workers;
    Map<String, WSWorker> workersById;

    Set<Long> firsts = new HashSet<Long>();

    public WSListener() {
        workers = new HashMap<Long, WSWorker>();
        workersById = new HashMap<String, WSWorker>();
        System.out.println("init");
    }

    @org.glassfish.websocket.api.annotations.WebSocketMessage
    public void processPacket(String message, WebSocketWrapper wsw) {
        Long c = wsw.getConversation().getConversationID();
        //System.out.println("packet: "+message+" - "+wsw+" - "+wsw.getConversation().getConversationID());
        if (!firsts.contains(c)) {
        System.out.println(""+c+":"+message);
            firsts.add(c);
        }

        String s = message;
        WSWorker w = workers.get(c);

        Gson g = new Gson();
        
        if (w == null) {
            InitPacket i = g.fromJson(s, InitPacket.class);

            if (INIT_NEW.equals(i.getCmd())) {
                System.out.println("new worker for "+i.getId());
                w = new WSWorker(wsw, i.getId());
                workersById.put(i.getId(), w);

            } else if (INIT_JOIN.equals(i.getCmd())) {
                w = workersById.get(i.getId());
                if (w == null) {
                    System.err.println("Worker for second does not exist: "+i.getId());
                    return;
                }
                System.out.println("joined worker for "+i.getId());
                w.setSecond(wsw);

            } else {
                System.err.println("Unknown init packet: "+s);
                return;
            }

            workers.put(c, w);
            return;
        }
        
        PosPacket p = g.fromJson(s, PosPacket.class);
        w.processPacket(p, wsw);
    }


    @org.glassfish.websocket.api.annotations.WebSocketOpen
    public void processOpen(WebSocketWrapper wsw) {
        System.out.println("open: "+wsw);
    }
    @org.glassfish.websocket.api.annotations.WebSocketClose
    public void processClose(WebSocketWrapper wsw) {
        System.out.println("close: "+wsw);
    }
    @org.glassfish.websocket.api.annotations.WebSocketError
    public void processError(Exception ex, WebSocketWrapper wsw) {
        System.out.println("error: "+wsw);
        ex.printStackTrace();
    }



    @WebSocketOpen
    public void processOpened(Session session, EndpointConfiguration cfg) {
        System.out.println("o opened: "+session+" - "+cfg);
    }

    @WebSocketMessage
    public void processPacket(String message, Session session) {
        System.out.println("o process packet: "+message+" - "+session);
    }
    @WebSocketClose
    public void processClosed(Session session) {
        System.out.println("o closed: "+session);
    }
    @WebSocketError
    public void processError(Session session, Throwable error) {
        System.out.println("o error: "+error+" - "+session);
    }

}
