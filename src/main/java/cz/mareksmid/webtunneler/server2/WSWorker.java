package cz.mareksmid.webtunneler.server2;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.ScenePacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import javax.websocket.Session;

class WSWorker {

    private static SceneGen sg = new SceneGen();

    private boolean assigned = false;
    private Session first, second = null;
    private String id;
    private Scene scene;

    private static ObjectMapper mapper = new ObjectMapper();

    public WSWorker(Session c, String id) {
        first = c;
        this.id = id;
    }

    public void processPacket(PosPacket pp, Session c) {
        if (!assigned) {return;}

        synchronized(scene) {
            try {
                if (c.equals(first)) {
                    first.getBasicRemote().sendObject(mapper.writeValueAsString(scene.update(pp, true)));
                } else {
                    second.getBasicRemote().sendObject(mapper.writeValueAsString(scene.update(pp, false)));
                }
                
                if (scene.isExploded() != 0) {
                    InitPacket ip = new InitPacket();
                    ip.setId(id);
                    ip.setCmd("EXPL");
                    InitPacket ipe = new InitPacket();
                    ipe.setId(id);
                    ipe.setCmd("EEXPL");
                    if (scene.isExploded() == 1) {
                        first.getBasicRemote().sendObject(mapper.writeValueAsString(ip));
                        second.getBasicRemote().sendObject(mapper.writeValueAsString(ipe));
                    } else if (scene.isExploded() == 2) {
                        first.getBasicRemote().sendObject(mapper.writeValueAsString(ipe));
                        second.getBasicRemote().sendObject(mapper.writeValueAsString(ip));
                    }
                    init();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setSecond(Session c) {
        if (assigned) {throw new RuntimeException("Already assigned");}
        second = c;
        init();
        assigned = true;
    }

    private void init() {
        scene = sg.generateScene();
        ScenePacket[] scenes = sg.getScenePackets(scene);

        try {
            first.getBasicRemote().sendObject(mapper.writeValueAsString(scenes[0]));
            second.getBasicRemote().sendObject(mapper.writeValueAsString(scenes[1]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
