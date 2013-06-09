package md.frolov.legume.client.elastic.model;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import md.frolov.legume.client.elastic.model.request.*;
import md.frolov.legume.client.elastic.model.response.HealthStatus;
import md.frolov.legume.client.elastic.model.response.PingResponse;
import md.frolov.legume.client.elastic.model.response.SearchHits;
import md.frolov.legume.client.elastic.model.response.SearchResponse;

@AutoBeanFactory.Category(QueryStringCategory.class)
public interface ModelFactory extends AutoBeanFactory {
    ModelFactory INSTANCE = GWT.create(ModelFactory.class);

    AutoBean <SearchHits> htis();

    AutoBean<SearchResponse> searchResponse();
    AutoBean<PingResponse> pingResponse();

    AutoBean<HealthStatus> healthStatus();

    AutoBean<ElasticSearchRequest> elasticSearchRequest();
    AutoBean<QueryString> queryString();
    AutoBean<QueryString.QueryStringDef> queryStringDef();

    AutoBean<RangeFacet> rangeFacet();
    AutoBean<RangeFacet.RangeInt> rangeFacetRangeInt();
    AutoBean<RangeFacet.RangeStr> rangeFacetRangeStr();
    AutoBean<RangeFacet.RangeDate> rangeFacetRangeDate();

    AutoBean<TermFacet> termFacet();
    AutoBean<TermFacet.TermsDef> termFacetDef();

    AutoBean<DateHistogramFacet> dateHistogramFacet();
    AutoBean<DateHistogramFacet.DateHistogramFacetDef> dateHistogramFacetDef();

    AutoBean<FilteredQuery> filteredQuery();
    AutoBean<FilteredQuery.FilteredQueryDef> filteredQueryDef();

    AutoBean<QueryFilter> queryFilter();

    AutoBean<AndFilter> andFilter();

    AutoBean<RangeFilter> rangeFilter();
    AutoBean<RangeFilter.RangeFilterDef> rangeFilterDef();

    AutoBean<MatchAllQuery> matchAllQuery();
}
