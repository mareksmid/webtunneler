/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
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

    public WSWorker(Session c, String id) {
        first = c;
        this.id = id;
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //if (pp.getOr() == Const.ORIENTATION_EXPLODED) {
            if (scene.isExploded()) {
                init();
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

        SceneGen sg = new SceneGen();
        ScenePacket[] scenes = sg.generatePackets();

        Gson g = new Gson();
        try {
            first.getBasicRemote().sendObject(g.toJson(scenes[0]));
            second.getBasicRemote().sendObject(g.toJson(scenes[1]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        scene = sg.generateScene();
        scene.init();
    }

}
