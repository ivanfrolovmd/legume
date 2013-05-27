package md.frolov.legume.client.elastic.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface LogEvent {
    @PropertyName("@source")
    String getSource();

    @PropertyName("@tags")
    List<String> getTags();

    @PropertyName("@fields")
    Map<String, List<String>> getFields(); // TODO what about numbers?

    @PropertyName("@timestamp")
    Date getTimestamp();

    @PropertyName("@source_host")
    String getSourceHost();

    @PropertyName("@source_path")
    String getSourcePath();

    @PropertyName("@message")
    String getMessage();

    @PropertyName("@type")
    String getType();
}
