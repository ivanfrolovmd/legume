package md.frolov.legume.client.elastic.model.query;

import java.util.Map;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ElasticSearchQuery
{
    Query getQuery();
    void setQuery(Query query);

    Filter getFilter();
    void setFilter(Filter filter);

    Map<String, SortOrder> getSort();
    void setSort(Map<String, SortOrder> sort);

    int getTimeout();
    void setTimeout(int timeout);

    Long getFrom();
    void setFrom(Long Integer);

    Integer getSize();
    void setSize(Integer size);

    @AutoBean.PropertyName("search_type")
    String getSearchType();
    @AutoBean.PropertyName("search_type")
    void setSearchType(String searchType);

    Map<String, Facet> getFacets();
    void setFacets(Map<String, Facet> facets);
}
