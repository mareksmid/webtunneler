package cz.mareksmid.webtunneler.server2;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ServerEndpoint(value="/wts", encoders = WSListener.StringEncoder.class)
public class WSListener {
    
    private static final Logger log = LoggerFactory.getLogger(WSListener.class);

    public static final String INIT_NEW = "NEW";
    public static final String INIT_JOIN = "JOIN";

    private static CommBean commBean = new CommBean();
    private static ObjectMapper mapper = new ObjectMapper();

    @OnMessage
    public void processPacket(String message, Session sess) {
        String  c = sess.getId();

        String s = message;
        WSWorker w = commBean.getWorker(c);

        try {
            if (w == null) {
                InitPacket i = mapper.readValue(s, InitPacket.class);

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
            } else {
                PosPacket p = mapper.readValue(s, PosPacket.class);
                w.processPacket(p, sess);
            }
        } catch (IOException ex) {
            log.warn("Failed to parse packet", ex);
        }
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

    public static class StringEncoder implements Encoder.Text<String> {
        @Override
        public String encode(String message) {
            return message;
        }

        @Override
        public void init(EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }
    }

}
