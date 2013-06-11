package md.frolov.legume.client.elastic.model.reply;

import java.util.Map;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface Mapping extends WrappedMap
{
    Map<String, Map<String, TypeMapping>> getObj();

    interface TypeMapping {
        Map<String, PropertyMapping> getProperties();
    }

    interface PropertyMapping
    {
        String getType();
        String getFormat();
        String getDynamic();
        Map<String, PropertyMapping> getProperties();
    }
}
