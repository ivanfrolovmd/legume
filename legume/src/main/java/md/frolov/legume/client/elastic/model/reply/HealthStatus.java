package md.frolov.legume.client.elastic.model.reply;

public interface HealthStatus {
    String getCluster_name();

    String getStatus();

    Boolean getTimed_out();

    Integer getNumber_of_nodes();

    Integer getNumber_of_data_nodes();

    Integer getActive_primary_shards();

    Integer getActive_shards();

    Integer getRelocating_shards();

    Integer getInitializing_shards();

    String getUnassigned_shards();
}
