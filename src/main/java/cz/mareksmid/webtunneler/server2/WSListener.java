package cz.mareksmid.webtunneler.server2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ServerEndpoint(value="/wts", encoders = WSListener.JsonEncoder.class)
public class WSListener {
    
    private static final Logger log = LoggerFactory.getLogger(WSListener.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new ParameterNamesModule());
    }

    public static final String INIT_NEW = "NEW";
    public static final String INIT_JOIN = "JOIN";

    private static CommBean commBean = new CommBean();

    @OnMessage
    public void processPacket(String msg, Session sess) {
        String  c = sess.getId();
        WSWorker w = commBean.getWorker(c);
        if (w == null) {
            commBean.putWorker(c, createWorker(msg, sess));
        } else {
            w.processPacket(decode(msg, PosPacket.class), sess);
        }

    }

    private WSWorker createWorker(String msg, Session sess) {
        InitPacket i = decode(msg, InitPacket.class);
        if (INIT_NEW.equals(i.getCmd())) {
            log.info("new worker for " + i.getId());
            WSWorker w = new WSWorker(sess, i.getId());
            commBean.putWorkersById(i.getId(), w);
            return w;
        } else if (INIT_JOIN.equals(i.getCmd())) {
            WSWorker w = commBean.getWorkersById(i.getId());
            if (w == null) {
                log.warn("Worker for second does not exist: " + i.getId());
                return null;
            }
            log.info("joined worker for " + i.getId());
            w.setSecond(sess);
            return w;
        } else {
            log.warn("Unknown init packet: " + i);
            throw new RuntimeException("Unknown init packet: " + i);
        }
    }

    private <T> T decode(String msg, Class<T> type) {
        try {
            return mapper.readValue(msg, type);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot decode packet: " + msg, ex);
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

    public static class JsonEncoder implements Encoder.Text<Object> {
        @Override
        public void init(EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }

        @Override
        public String encode(Object object) throws EncodeException {
            try {
                return mapper.writeValueAsString(object);
            } catch (JsonProcessingException ex) {
                throw new EncodeException(object, "Cannot encode into JSON", ex);
            }
        }
    }
}
