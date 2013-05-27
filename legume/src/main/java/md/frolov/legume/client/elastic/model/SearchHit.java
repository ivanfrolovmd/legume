package md.frolov.legume.client.elastic.model;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SearchHit {
    @PropertyName("_index")
    String getIndex();

    @PropertyName("_type")
    String getType();

    @PropertyName("_id")
    String getId();

    @PropertyName("_score")
    String getScore();

    @PropertyName("_source")
    LogEvent getLogEvent();
}
