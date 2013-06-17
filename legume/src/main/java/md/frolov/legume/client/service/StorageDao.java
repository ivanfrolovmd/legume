package md.frolov.legume.client.service;

import java.util.Map;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface StorageDao
{
    String get(String key);
    void put(String key, String value);
    Map<String, String> getAllProperties();
}
