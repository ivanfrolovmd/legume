package md.frolov.legume.client.elastic.model.request;

import java.util.Map;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface MatchAllQuery extends Query
{
    @AutoBean.PropertyName("match_all")
    Map<Void,Void> getMatchAll();
    @AutoBean.PropertyName("match_all")
    void setMatchAll(Map<Void,Void> matchAll);
}
