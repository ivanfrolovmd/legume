package md.frolov.jettyrunner;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Params
{
    @Parameter(names = {"--port","-p"},description = "Port to listen to")
    public Integer port = 8080;

    @Parameter(names={"--host","-h"}, description = "Host to bind to")
    public String host;

    @Parameter(names="--properties", description = "JS file with properties", converter = FileConverter.class)
    public File propertiesJs;

    @Parameter(names = "--help", help = true)
    public boolean help = false;
}
