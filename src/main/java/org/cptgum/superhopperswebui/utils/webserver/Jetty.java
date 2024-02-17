package org.cptgum.superhopperswebui.utils.webserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.cptgum.superhopperswebui.utils.LoggerUtils;

public class Jetty {
    private static Server server;


    public static void start(int port) throws Exception {

        server = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("plugins/SuperHoppersWebUI/web");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new org.eclipse.jetty.server.Handler[] { resourceHandler });

        server.setHandler(handlers);

        Thread jettyThread = new Thread(() -> {
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                LoggerUtils.logError("Failed to start Jetty Server: "+ e);
            }
        });
        jettyThread.start();
    }
    public static void stop() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }
}
