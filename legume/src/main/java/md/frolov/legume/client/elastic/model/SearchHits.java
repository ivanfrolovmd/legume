package md.frolov.legume.client.elastic.model;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SearchHits {
    long getTotal();

    @PropertyName("max_score")
    double getMaxScore();

    List<SearchHit> getHits();
}
