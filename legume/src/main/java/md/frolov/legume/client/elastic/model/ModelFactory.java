package md.frolov.legume.client.elastic.model;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import md.frolov.legume.client.configuration.ConnectionsConf;
import md.frolov.legume.client.elastic.model.query.*;
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.elastic.model.reply.HealthStatus;
import md.frolov.legume.client.elastic.model.reply.Mapping;
import md.frolov.legume.client.elastic.model.reply.PingReply;
import md.frolov.legume.client.elastic.model.reply.SearchHits;

@AutoBeanFactory.Category(QueryStringCategory.class)
public interface ModelFactory extends AutoBeanFactory {
    ModelFactory INSTANCE = GWT.create(ModelFactory.class);

    AutoBean <SearchHits> htis();

    AutoBean<ElasticSearchReply> elasticSearchReply();
    AutoBean<PingReply> pingReply();

    AutoBean<HealthStatus> healthStatus();

    AutoBean<ElasticSearchQuery> elasticSearchQuery();
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

    AutoBean<LimitFilter> limitFilter();
    AutoBean<LimitFilter.LimitFilterDef> limitFilterDef();

    AutoBean<MatchAllQuery> matchAllQuery();

    AutoBean<Mapping> mapping();
    AutoBean<Mapping.TypeMapping> typeMapping();
    AutoBean<Mapping.PropertyMapping> propertyMapping();

    AutoBean<ConnectionsConf> connectionsConf();
}
