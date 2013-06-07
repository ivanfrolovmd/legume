package md.frolov.legume.client.elastic.model;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import md.frolov.legume.client.elastic.model.request.ElasticSearchRequest;
import md.frolov.legume.client.elastic.model.request.QueryString;
import md.frolov.legume.client.elastic.model.request.QueryStringCategory;
import md.frolov.legume.client.elastic.model.request.RangeFacet;
import md.frolov.legume.client.elastic.model.request.SearchQuery;
import md.frolov.legume.client.elastic.model.request.TermFacet;
import md.frolov.legume.client.elastic.model.response.HealthStatus;
import md.frolov.legume.client.elastic.model.response.SearchHits;
import md.frolov.legume.client.elastic.model.response.SearchResponse;

@AutoBeanFactory.Category(QueryStringCategory.class)
public interface ModelFactory extends AutoBeanFactory {
    ModelFactory INSTANCE = GWT.create(ModelFactory.class);

    AutoBean <SearchHits> htis();

    AutoBean<SearchResponse> response();

    AutoBean<HealthStatus> healthStatus();

    AutoBean<ElasticSearchRequest> elasticSearchRequest();
    AutoBean<SearchQuery> searchQuery();
    AutoBean<QueryString> queryString();

    AutoBean<RangeFacet> rangeFacet();
    AutoBean<RangeFacet.RangeInt> rangeFacetRangeInt();
    AutoBean<RangeFacet.RangeStr> rangeFacetRangeStr();
    AutoBean<RangeFacet.RangeDate> rangeFacetRangeDate();

    AutoBean<TermFacet> termFacet();
    AutoBean<TermFacet.TermsDef> termFacetDef();
}
