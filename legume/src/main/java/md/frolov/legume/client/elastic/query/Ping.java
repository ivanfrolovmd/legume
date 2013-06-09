package md.frolov.legume.client.elastic.query;

import com.google.gwt.http.client.RequestBuilder;

import md.frolov.legume.client.elastic.model.request.ElasticSearchRequest;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Ping implements RequestQuery
{
    @Override
    public String getUri()
    {
        return "";
    }

    @Override
    public ElasticSearchRequest getPayload()
    {
        return null;
    }

    @Override
    public RequestBuilder.Method getMethod()
    {
        return RequestBuilder.GET;
    }
}
