package md.frolov.legume.client.elastic.query;

import com.google.gwt.http.client.RequestBuilder;

import md.frolov.legume.client.elastic.model.query.ElasticSearchRequest;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface RequestQuery
{
    String getUri();
    ElasticSearchRequest getPayload();
    RequestBuilder.Method getMethod();
}
