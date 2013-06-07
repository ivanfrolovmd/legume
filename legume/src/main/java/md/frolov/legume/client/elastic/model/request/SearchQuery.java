package md.frolov.legume.client.elastic.model.request;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface SearchQuery extends Query
{
    @AutoBean.PropertyName("query_string")
    QueryString getQueryString();

    @AutoBean.PropertyName("query_string")
    void setQueryString(QueryString queryString);
}
