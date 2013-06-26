/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.InitPacket;
import cz.mareksmid.webtunneler.server2.json.ScenePacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import javax.websocket.Session;

/**
 *
 * @author marek
 */
class WSWorker {
    
    private boolean assigned = false;
    private Session first, second = null;
    private String id;
    private Scene scene;
    private SceneGen sg;
    
    public WSWorker(Session c, String id) {
        first = c;
        this.id = id;
        sg = new SceneGen();
    }

    public void processPacket(PosPacket pp, Session c) {
        if (!assigned) {return;}

        synchronized(scene) {
            try {
                if (c.equals(first)) {
                    first.getBasicRemote().sendObject(new Gson().toJson(scene.update(pp, true)));
                } else {
                    second.getBasicRemote().sendObject(new Gson().toJson(scene.update(pp, false)));
                }
                
                if (scene.isExploded() != 0) {
                    InitPacket ip = new InitPacket();
                    ip.setId(id);
                    ip.setCmd("EXPL");
                    InitPacket ipe = new InitPacket();
                    ipe.setId(id);
                    ipe.setCmd("EEXPL");
                    if (scene.isExploded() == 1) {
                        first.getBasicRemote().sendObject(new Gson().toJson(ip));
                        second.getBasicRemote().sendObject(new Gson().toJson(ipe));
                    } else if (scene.isExploded() == 2) {
                        first.getBasicRemote().sendObject(new Gson().toJson(ipe));
                        second.getBasicRemote().sendObject(new Gson().toJson(ip));
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

    public boolean isAssigned() {
        return assigned;
    }

    private void init() {
        scene = sg.generateScene();
        ScenePacket[] scenes = sg.getScenePackets(scene);

        Gson g = new Gson();
        try {
            first.getBasicRemote().sendObject(g.toJson(scenes[0]));
            second.getBasicRemote().sendObject(g.toJson(scenes[1]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
