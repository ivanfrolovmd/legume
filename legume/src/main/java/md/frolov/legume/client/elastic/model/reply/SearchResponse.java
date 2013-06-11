package md.frolov.legume.client.elastic.model.reply;

import java.util.Map;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SearchResponse {
    long getTook();

    @PropertyName("timed_out")
    boolean isTimedOut();

    @PropertyName("_shards")
    Shards getShards();

    SearchHits getHits();

    Map<String, Facet> getFacets();

}
