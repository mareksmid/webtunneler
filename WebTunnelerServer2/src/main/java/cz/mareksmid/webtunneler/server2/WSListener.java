package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;

import javax.inject.Inject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value="/wts")
public class WSListener {
    
    private static final Logger log = LoggerFactory.getLogger(WSListener.class);

    public static final String INIT_NEW = "NEW";
    public static final String INIT_JOIN = "JOIN";

    @Inject
    private CommBean commBean;

    @OnMessage
    public void processPacket(String message, Session sess) {
        String  c = sess.getId();
        //System.out.println("packet: "+message+" - "+wsw+" - "+wsw.getConversation().getConversationID());
        /*if (!firsts.contains(c)) {
            System.out.println(""+c+":"+message);
            firsts.add(c);
        }*/

        String s = message;
        WSWorker w = commBean.getWorker(c);

        Gson g = new Gson();
        
        if (w == null) {
            InitPacket i = g.fromJson(s, InitPacket.class);

            if (INIT_NEW.equals(i.getCmd())) {
                log.info("new worker for "+i.getId());
                w = new WSWorker(sess, i.getId());
                commBean.putWorkersById(i.getId(), w);

            } else if (INIT_JOIN.equals(i.getCmd())) {
                w = commBean.getWorkersById(i.getId());
                if (w == null) {
                    log.warn("Worker for second does not exist: "+i.getId());
                    return;
                }
                log.info("joined worker for "+i.getId());
                w.setSecond(sess);

            } else {
                log.warn("Unknown init packet: "+s);
                return;
            }

            commBean.putWorker(c, w);
            return;
        }
        
        PosPacket p = g.fromJson(s, PosPacket.class);
        w.processPacket(p, sess);
    }


    @OnOpen
    public void processOpened(Session session) {
        log.info("Socket opened: session "+session);
    }

    @OnClose
    public void processClosed(Session session) {
        log.info("Socket opened: session "+session);
    }
    @OnError
    public void processError(Session session, Throwable error) {
        log.warn("Socket error: "+error+" from session "+session);
        error.printStackTrace();
    }

}
