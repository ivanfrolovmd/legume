package md.frolov.legume.client.service.impl;

import java.util.Map;

import com.google.common.collect.Maps;

import md.frolov.legume.client.service.StorageDao;
import md.frolov.legume.client.util.JsMap;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class JsPropertiesStorageDao implements StorageDao
{
    @Override
    public String get(final String key)
    {
        return getFromProperties(key);
    }

    @Override
    public void put(final String key, final String value)
    {
        setToMemory(key, value);
    }

    @Override
    public Map<String, String> getAllProperties()
    {
        Map<String, String> result = Maps.newHashMap();
        JsMap jsMap = getFromProperties();
        for(String key: jsMap.keySet()) {
            String value = jsMap.get(key);
            result.put(key, value);
        }
        return result;
    }

    private final native String getFromProperties(final String key) /*-{
        return $wnd.window.GlobalProperties[key].toString();
    }-*/;

    private final native void setToMemory(final String key, final String value) /*-{
        return $wnd.window.GlobalProperties[key] = value;
    }-*/;

    private final native JsMap getFromProperties() /*-{
        return $wnd.window.GlobalProperties;
    }-*/;
}
