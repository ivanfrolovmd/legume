package md.frolov.legume.client.service.impl;

import javax.inject.Singleton;

import md.frolov.legume.client.service.ConfigurationService;

import com.google.gwt.user.client.Cookies;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService{

    @Override
    public String get(String key) {
        return get(key, "");
    }

    @Override
    public String get(String key, String defaultValue) {
        String value = Cookies.getCookie(key);
        if(value==null) {
            value = getFromProperties(key);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    @Override
    public void put(String key, String value) {
        Cookies.setCookie(key, value); //TODO secure cookies?
    }

    private final native String getFromProperties(String key) /*-{
		return $wnd.GlobalProperties[key];
    }-*/;
}
