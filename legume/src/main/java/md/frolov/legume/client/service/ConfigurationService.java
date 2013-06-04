package md.frolov.legume.client.service;

import java.util.Map;

public interface ConfigurationService {
    String get(String key);

    String get(String key, String defaultValue);

    int getInt(String key);

    void put(String key, String value);

    Map<String, String> getPropertyMap();
}
