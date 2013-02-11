package md.frolov.legume.client.service;

public interface ConfigurationService {
    String get(String key);

    String get(String key, String defaultValue);

    void put(String key, String value);
}
