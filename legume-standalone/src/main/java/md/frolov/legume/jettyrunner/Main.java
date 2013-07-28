package md.frolov.legume.jettyrunner;

import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.beust.jcommander.JCommander;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Main
{
    public static void main(String[] args)
    {
        Params params = new Params();
        JCommander commander = new JCommander(params, args);

        if (params.help)
        {
            commander.usage();
            System.exit(0);
        }

        // Start a Jetty server with some sensible(?) defaults
        try
        {
            Server srv = new Server();
            srv.setStopAtShutdown(true);

            // Allow 5 seconds to complete.
            // Adjust this to fit with your own webapp needs.
            // Remove this if you wish to shut down immediately (i.e. kill <pid> or Ctrl+C).
            srv.setGracefulShutdown(5000);

            // Increase thread pool
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setMaxThreads(100);
            srv.setThreadPool(threadPool);

            // Ensure using the non-blocking connector (NIO)
            Connector connector = new SelectChannelConnector();
            connector.setPort(params.port);
            connector.setMaxIdleTime(30000);
            if (params.host != null)
            {
                connector.setHost(params.host);
            }
            srv.setConnectors(new Connector[]{connector});

            // Get the war-file
            ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
            String warFile = protectionDomain.getCodeSource().getLocation().toExternalForm();
            String currentDir = new File(protectionDomain.getCodeSource().getLocation().getPath()).getParent();

            // Handle signout/signin in BigIP-cluster

            // Add the warFile (this jar)
            WebAppContext context = new WebAppContext(warFile, "/");
            context.setServer(srv);

            if (params.propertiesJs != null)
            {
                setupJsPropertiesServlet(params.propertiesJs, context, "/properties.js");
            }

            // Add the handlers
            HandlerList handlers = new HandlerList();
            handlers.addHandler(context);
            srv.setHandler(handlers);

            srv.start();
            srv.join();
        }
        catch (Exception e)
        {
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
