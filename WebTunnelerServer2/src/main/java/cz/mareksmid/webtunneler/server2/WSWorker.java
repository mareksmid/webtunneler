/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import com.google.gson.Gson;
import cz.mareksmid.webtunneler.server2.json.ScenePacket;
import cz.mareksmid.webtunneler.server2.json.PosPacket;

/**
 *
 * @author marek
 */
class WSWorker {
    
    private boolean assigned = false;
    private String first, second = null;
    private String id;
    private Scene scene;

    public WSWorker(String c, String id) {
        first = c;
        this.id = id;
    }

    public void processPacket(PosPacket pp, String c) {
        if (!assigned) {return;}

        //WebSocketPacket p = new RawPacket(new Gson().toJson(pp));

        if (c.equals(first)) {
            //first.sendPacket(new RawPacket(new Gson().toJson(scene.updateFirst(pp))));
        } else {
            //second.sendPacket(new RawPacket(new Gson().toJson(scene.updateSecond(pp))));
        }
        //System.out.println(">"+s);

        //if (s.equals("EXPL")) {
        if (pp.getOr() == PosPacket.ORIENTATION_EXPLODED) {
            init();
        }
    }

    public void setSecond(String c) {
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
        //WebSocketPacket p1 = new RawPacket(g.toJson(scenes[0]));
        //WebSocketPacket p2 = new RawPacket(g.toJson(scenes[1]));
        //first.sendPacket(p1);
        //second.sendPacket(p2);
        scene = sg.generateScene();
        scene.init();
    }

}
