package md.frolov.legume.client.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.Cookies;

import md.frolov.legume.client.service.StorageDao;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class CookieStorageDao implements StorageDao
{
    private static final String PREFIX = "_legume_conf_";
    private static final Date NEVER = new Date(Long.MAX_VALUE);

    @Override
    public String get(final String key)
    {
        return Cookies.getCookie(PREFIX + key);
    }

    @Override
    public void put(final String key, final String value)
    {
        Cookies.setCookie(PREFIX + key, value, NEVER);
    }

    @Override
    public Map<String, String> getAllProperties()
    {
        Map<String,String> result = Maps.newHashMap();
        Collection <String> keys = Cookies.getCookieNames();
        for (String key : keys)
        {
            if(!key.startsWith(PREFIX)) {
                continue;
            }
            String realKey = key.substring(PREFIX.length());
            String value = Cookies.getCookie(key);
            result.put(realKey, value);
        }
        return result;
    }

}
