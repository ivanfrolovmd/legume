package md.frolov.legume.client.elastic.query;

import md.frolov.legume.client.elastic.model.request.ElasticSearchRequest;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface RequestQuery
{
    String getUri();
    ElasticSearchRequest getPayload();
}
