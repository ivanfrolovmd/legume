package md.frolov.legume.client.service;

import java.util.Map;

public interface ConfigurationService {
    String get(String key);

    String get(String key, String defaultValue);

    int getInt(String key);

    <T> T getObject(String key, Class<T> clazz);

    void put(String key, String value);

    <T> void put(String key, T object);

    Map<String, String> getPropertyMap();
}
