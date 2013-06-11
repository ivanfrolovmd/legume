package md.frolov.legume.client.elastic.api;

import java.util.Map;

import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.autobean.shared.AutoBean;

import md.frolov.legume.client.elastic.model.reply.Mapping;
import md.frolov.legume.client.elastic.model.reply.WrappedMap;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class MappingsRequest extends ESRequest<Void, Mapping, MappingsResponse>
{
    @Override
    public String getUri()
    {
        return "/_mapping";
    }

    @Override
    public RequestBuilder.Method getMethod()
    {
        return RequestBuilder.GET;
    }

    @Override
    public AutoBean<Void> getPayload()
    {
        return null;
    }

    @Override
    public AutoBean<Mapping> getExpectedAutobeanResponse()
    {
        return factory.mapping();
    }

    @Override
    public MappingsResponse getResponse(final Mapping mappings)
    {
        return new MappingsResponse(mappings);
    }
}
