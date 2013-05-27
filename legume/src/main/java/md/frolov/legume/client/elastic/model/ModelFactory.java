package md.frolov.legume.client.elastic.model;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface ModelFactory extends AutoBeanFactory {
    AutoBean<SearchHits> htis();

    AutoBean<SearchResponse> response();

    AutoBean<HealthStatus> healthStatus();
}
