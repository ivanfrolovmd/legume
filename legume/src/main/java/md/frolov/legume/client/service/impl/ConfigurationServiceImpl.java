package md.frolov.legume.client.service.impl;

import javax.inject.Singleton;

import com.google.gwt.user.client.Cookies;

import md.frolov.legume.client.service.ConfigurationService;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService
{
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
    public void put(final String key, final String value)
    {
        Cookies.setCookie(key, value); //TODO secure cookies?
    }

    private final native String getFromProperties(final String key) /*-{
        return $wnd.window.GlobalProperties[key];
        //TODO check for null values
    }-*/;
}
