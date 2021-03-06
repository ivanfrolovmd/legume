package md.frolov.legume.client.model;

import java.util.Map;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ConnectionsConf
{
    Map<String, String> getConnections();
    void setConnections(Map<String,String> connections);
}
