package md.frolov.legume.client.elastic.api;

import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.autobean.shared.AutoBean;

import md.frolov.legume.client.elastic.model.ModelFactory;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public abstract class ESRequest<QUERY, REPLY, RESPONSE extends ESResponse<REPLY>>
{
    protected final static ModelFactory factory = ModelFactory.INSTANCE;

    public abstract String getUri();
    public RequestBuilder.Method getMethod(){
        return RequestBuilder.POST;
    }
    public abstract AutoBean<QUERY> getPayload();
    public abstract AutoBean<REPLY> getExpectedAutobeanResponse();
    public abstract RESPONSE getResponse(REPLY reply);
}
