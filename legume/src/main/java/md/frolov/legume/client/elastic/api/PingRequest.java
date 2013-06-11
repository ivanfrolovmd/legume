package md.frolov.legume.client.elastic.api;

import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.autobean.shared.AutoBean;

import md.frolov.legume.client.elastic.model.reply.PingReply;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class PingRequest extends ESRequest<Void, PingReply, PingResponse>
{

    @Override
    public String getUri()
    {
        return "";
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
    public AutoBean<PingReply> getExpectedAutobeanResponse()
    {
        return factory.pingReply();
    }

    @Override
    public PingResponse getResponse(final PingReply pingReply)
    {
        return new PingResponse(pingReply);
    }
}
