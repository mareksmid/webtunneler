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

    public WSWorker(Session c, String id) {
        first = c;
        this.id = id;
    }

    public void processPacket(PosPacket pp, Session c) {
        if (!assigned) {return;}

        synchronized(scene) {
            try {
                if (c.equals(first)) {
                    first.getBasicRemote().sendObject(scene.update(pp, true));
                } else {
                    second.getBasicRemote().sendObject(scene.update(pp, false));
                }
                
                if (scene.isExploded() != 0) {
                    InitPacket ip = new InitPacket(id, "EXPL");
                    InitPacket ipe = new InitPacket(id, "EEXPL");
                    if (scene.isExploded() == 1) {
                        first.getBasicRemote().sendObject(ip);
                        second.getBasicRemote().sendObject(ipe);
                    } else if (scene.isExploded() == 2) {
                        first.getBasicRemote().sendObject(ipe);
                        second.getBasicRemote().sendObject(ip);
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
            first.getBasicRemote().sendObject(scenes[0]);
            second.getBasicRemote().sendObject(scenes[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
