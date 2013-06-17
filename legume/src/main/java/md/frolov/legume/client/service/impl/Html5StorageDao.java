package md.frolov.legume.client.service.impl;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gwt.storage.client.Storage;

import md.frolov.legume.client.service.StorageDao;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Html5StorageDao implements StorageDao
{
    private final Storage storage = Storage.getLocalStorageIfSupported();

    @Override
    public String get(final String key)
    {
        return storage.getItem(key);
    }

    @Override
    public void put(final String key, final String value)
    {
        storage.setItem(key, value);
    }

    @Override
    public Map<String, String> getAllProperties()
    {
        Map<String, String> result = Maps.newHashMap();
        for (int i = 0; i < storage.getLength(); i++)
        {
            String key = storage.key(i);
            String value = storage.getItem(key);
            result.put(key, value);
        }
        return result;
    }
}
