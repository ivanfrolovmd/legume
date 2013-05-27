package md.frolov.legume.client.elastic.model;

public interface Shards {
    long getTotal();

    long getSuccessful();

    long getFailed();
}
