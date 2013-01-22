/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.ScenePacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;
import org.glassfish.websocket.platform.WebSocketWrapper;

/**
 *
 * @author marek
 */
class WSWorker {
    
    private boolean assigned = false;
    private WebSocketWrapper first, second = null;
    private String id;
    private Scene scene;

    public WSWorker(WebSocketWrapper c, String id) {
        first = c;
        this.id = id;
    }

    public void processPacket(PosPacket pp, WebSocketWrapper c) {
        if (!assigned) {return;}

        try {
            if (c.equals(first)) {
                first.sendMessage(new Gson().toJson(scene.updateFirst(pp)));
            } else {
                second.sendMessage(new Gson().toJson(scene.updateSecond(pp)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //System.out.println(">"+s);

        if (pp.getOr() == PosPacket.ORIENTATION_EXPLODED) {
            init();
        }
    }

    public void setSecond(WebSocketWrapper c) {
        if (assigned) {throw new RuntimeException("Already assigned");}
        second = c;
        assigned = true;
        init();
    }

    public boolean isAssigned() {
        return assigned;
    }

    private void init() {

        SceneGen sg = new SceneGen();
        ScenePacket[] scenes = sg.generatePackets();

        Gson g = new Gson();
        try {
            first.sendMessage(g.toJson(scenes[0]));
            second.sendMessage(g.toJson(scenes[1]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        scene = sg.generateScene();
        scene.init();
    }

}
