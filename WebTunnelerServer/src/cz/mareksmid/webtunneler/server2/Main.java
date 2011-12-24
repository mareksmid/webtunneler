/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.mareksmid.webtunneler.server2;

import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.factory.JWebSocketFactory;

/**
 *
 * @author marek
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //System.out.println(">>"+System.getenv("JWEBSOCKET_HOME"));
        //String s = JWebSocketConfig.getConfigurationPath();
        //System.out.println(">>>"+s);

        JWebSocketFactory.start();
        //System.out.println(">"+JWebSocketFactory.getServers());

        WSListener l = new WSListener();

        WebSocketServer svr = JWebSocketFactory.getServer("cs0");
        svr.addListener(l);
        //System.out.println("conns:"+svr.getAllConnectors());
        //System.out.println("filterchain:"+svr.getFilterChain());
        //System.out.println("pluginchain:"+svr.getPlugInChain());
        try {
            svr.startServer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
