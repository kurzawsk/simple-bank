package com.kurzawsk.simple_bank;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kurzawsk.simple_bank.AppConfig.API_VERSION;

public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);
    private static Server server;

    public static void main(String[] args) {
        startService();
    }

    public static void startService() {
        server = new Server(8000);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");

        ServletHolder apiServletHolder = new ServletHolder(ServletContainer.class);
        apiServletHolder.setInitOrder(1);
        apiServletHolder.setInitParameter("javax.ws.rs.Application", AppConfig.class.getCanonicalName());

        String resourceBasePath = Application.class.getResource("/webapp").toExternalForm();
        contextHandler.setWelcomeFiles(new String[]{"index.html"});
        contextHandler.setResourceBase(resourceBasePath);
        ServletHolder swagger = new ServletHolder(new DefaultServlet());
        swagger.setInitOrder(2);

        contextHandler.addServlet(apiServletHolder, "/" + API_VERSION + "/*");
        contextHandler.addServlet(swagger, "/*");

        server.setHandler(contextHandler);
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Exception occurred while starting server", e);
        }
    }

    public static void stopService() throws Exception {
        server.stop();
        server.destroy();
    }


}
