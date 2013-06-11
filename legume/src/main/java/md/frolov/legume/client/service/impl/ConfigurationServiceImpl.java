package md.frolov.legume.client.service.impl;

import java.util.Collection;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.Cookies;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.service.ConfigurationService;
import md.frolov.legume.client.util.JsMap;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService
{
    @Inject
    private ModelFactory modelFactory;

    @Override
    public String get(final String key)
    {
        return get(key, "");
    }

    @Override
    public String get(final String key, final String defaultValue)
    {
        String value = Cookies.getCookie(key);
        if (value == null)
        {
            value = getFromProperties(key);
        }
        if (value == null)
        {
            value = defaultValue;
        }
        return value;
    }

    @Override
    public int getInt(final String key)
    {
        return Integer.valueOf(get(key, "0"));
    }

    @Override
    public void put(final String key, final String value)
    {
        Cookies.setCookie(key, value); //TODO secure cookies?
    }

    @Override
    public Map<String, String> getPropertyMap()
    {
        Map<String,String> properties = Maps.newTreeMap();

        JsMap jsMap = getFromProperties();
        for (String key : jsMap.keySet())
        {
            properties.put(key, jsMap.get(key));
        }

        Collection<String> cookieNames = Cookies.getCookieNames();
        for (String cookieName : cookieNames)
        {
            properties.put(cookieName, Cookies.getCookie(cookieName));
        }

        return properties;
    }

    @Override
    public <T> T getObject(final String key, final Class<T> clazz)
    {
        try{
            String json = get(key);
            return AutoBeanCodex.decode(modelFactory, clazz, json).as();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public <T> void put(final String key, final T object)
    {
        String json = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(object)).getPayload();
        put(key, json);
    }

    private final native String getFromProperties(final String key) /*-{
        return $wnd.window.GlobalProperties[key].toString();
        //TODO check for null values
    }-*/;

    private final native JsMap getFromProperties() /*-{
        return $wnd.window.GlobalProperties;
    }-*/;
}
