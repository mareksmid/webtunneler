package cz.mareksmid.webtunneler.server2.json;

public class InitPacket {

    private final String id;
    
    private final String cmd;

    public InitPacket(String id, String cmd) {
        this.id = id;
        this.cmd = cmd;
    }

    public String getId() {
        return id;
    }

    public String getCmd() {
        return cmd;
    }

}
