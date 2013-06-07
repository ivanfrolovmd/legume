package md.frolov.legume.client.elastic.model.response;

public interface Shards {
    long getTotal();

    long getSuccessful();

    long getFailed();
}
