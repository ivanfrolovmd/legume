package md.frolov.jettyrunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;

import org.apache.commons.io.FileUtils;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

import com.beust.jcommander.JCommander;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Main
{
    public static void main(String[] args) {
        Params params = new Params();
        JCommander commander = new JCommander(params, args);

        if(params.help) {
            commander.usage();
            System.exit(0);
        }

        Server server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(params.port);
        if(params.host!=null) {
            connector.setHost(params.host);
        }
        server.setConnectors(new Connector[]{connector});

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath("/");

        if(params.propertiesJs != null) {
            setupJsPropertiesServlet(params.propertiesJs, context, "/properties.js");
        }

        ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.addHandler(context);
        try {
            server.start();
            System.in.read();
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
      }

    private static void setupJsPropertiesServlet(File file, WebAppContext context, String pathSpec)
    {
        try
        {
            String contents = FileUtils.readFileToString(file);
            context.addServlet(new ServletHolder(new StringServlet(contents)), pathSpec);
        }
        catch (IOException e)
        {
            System.err.print("No such file");
            System.exit(100);
        }
    }
}
