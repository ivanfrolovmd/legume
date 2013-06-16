package md.frolov.legume.client.model;

import java.util.Map;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface GlobalConf
{
    Map<String, String> getProperties();
    void setProperties(Map<String, String> properties);
}
