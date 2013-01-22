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
import java.util.Map;

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
@WebSocketEndpoint(value="/ows", configuration = DefaultServerConfiguration.class)
@WebSocket(path = "/ws")
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
        System.out.println("o opened: "+session+" - "+cfg);
    }

    @WebSocketMessage
    public void processPacket(String message, Session session) {
        System.out.println("o process packet: "+message+" - "+session);
    }

    @org.glassfish.websocket.api.annotations.WebSocketMessage
    public void processPacket(String message, WebSocketWrapper wsw) {
        //System.out.println("packet: "+message+" - "+wsw+" - "+wsw.getConversation().getConversationID());
        System.out.println(""+wsw.getConversation().getConversationID()+":"+message);

        //String s = wsp.getString();
        String s = message;
        //WebSocketConnector c = wsse.getConnector();
        //WSWorker w = workers.get(c);
        String c = ""+wsw.getConversation().getConversationID();
        WSWorker w = workers.get(c);

        Gson g = new Gson();
        
        if (w == null) {
            InitPacket i = g.fromJson(s, InitPacket.class);

            if (i.getCmd().equals(INIT_NEW)) {
                System.out.println("new worker for "+i.getId());
                w = new WSWorker(wsw, i.getId());
                workersById.put(i.getId(), w);

            } else if (i.getCmd().equals(INIT_JOIN)) {
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

    @WebSocketClose
    public void processClosed(Session session) {
        System.out.println("o closed: "+session);
    }


    @WebSocketError
    public void processError(Session session, Throwable error) {
        System.out.println("o error: "+error+" - "+session);
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

}
