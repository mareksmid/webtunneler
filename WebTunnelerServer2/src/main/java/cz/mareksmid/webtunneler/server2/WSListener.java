/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author marek
 */
@ServerEndpoint(value="/wts")
public class WSListener {

    public static final String INIT_NEW = "NEW";
    public static final String INIT_JOIN = "JOIN";

    Map<String,WSWorker> workers;
    Map<String, WSWorker> workersById;

    Set<String> firsts = new HashSet<String>();

    public WSListener() {
        workers = new HashMap<String, WSWorker>();
        workersById = new HashMap<String, WSWorker>();
        System.out.println("init");
    }

    @OnMessage
    public void processPacket(String message, Session sess) {
        String  c = sess.getId();
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
                w = new WSWorker(sess, i.getId());
                workersById.put(i.getId(), w);

            } else if (INIT_JOIN.equals(i.getCmd())) {
                w = workersById.get(i.getId());
                if (w == null) {
                    System.err.println("Worker for second does not exist: "+i.getId());
                    return;
                }
                System.out.println("joined worker for "+i.getId());
                w.setSecond(sess);

            } else {
                System.err.println("Unknown init packet: "+s);
                return;
            }

            workers.put(c, w);
            return;
        }
        
        PosPacket p = g.fromJson(s, PosPacket.class);
        w.processPacket(p, sess);
    }


    @OnOpen
    public void processOpened(Session session) {
        System.out.println("o opened1: "+session);
    }

    @OnClose
    public void processClosed(Session session) {
        System.out.println("o closed: "+session);
    }
    @OnError
    public void processError(Session session, Throwable error) {
        System.out.println("o error: "+error+" - "+session);
    }

}
