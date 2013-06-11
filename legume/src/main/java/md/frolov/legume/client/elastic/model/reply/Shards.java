package md.frolov.legume.client.elastic.model.reply;

public interface Shards {
    long getTotal();

    long getSuccessful();

    long getFailed();
}
