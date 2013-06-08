package md.frolov.legume.client.elastic.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Sets;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import md.frolov.legume.client.Constants;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.model.request.ElasticSearchRequest;
import md.frolov.legume.client.elastic.query.RequestQuery;
import md.frolov.legume.client.service.ConfigurationService;

@Singleton
public class ElasticSearchServiceImpl implements ElasticSearchService, Constants
{

    private final static Logger LOG = Logger.getLogger("ElasticSearchServiceImpl");

    private final Set<Request> requests = Sets.newHashSet();

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ModelFactory modelFactory;

    @Override
    public <T> void query(final RequestQuery query, final AsyncCallback<T> callback, final Class<T> clazz)
    {
        queryRaw(query.getUri(),query.getPayload(), callback, clazz);
    }

    @Override
    public <T> void queryRaw(String uri, final ElasticSearchRequest request, final AsyncCallback<T> callback, final Class<T> clazz)
    {
        try
        {
            String url = configurationService.get(ELASTICSEARCH_SERVER) + uri;
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
            requestBuilder.setTimeoutMillis(configurationService.getInt(ELASTICSEARCH_TIMEOUT));
            if(request.getTimeout()==0) {
                request.setTimeout(configurationService.getInt(ELASTICSEARCH_TIMEOUT));
            }
            String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();
            Request xhrRequest = requestBuilder.sendRequest(data, new RequestCallback()
            {
                @Override
                public void onResponseReceived(final Request request, final Response response)
                {
                    if (response.getStatusCode() != 200)
                    {
                        throw new IllegalStateException("Invalid response code: " + response.getStatusCode());
                    }
                    T responseObj = AutoBeanCodex.decode(modelFactory, clazz, response.getText()).as();
                    callback.onSuccess(responseObj);
                }

                @Override
                public void onError(final Request request, final Throwable exception)
                {
                    callback.onFailure(exception);
                }
            });
            requests.add(xhrRequest);
        }
        catch (RequestException e)
        {
            callback.onFailure(e);
        }

    }

    @Override
    public void cancelAllRequests()
    {
        LOG.info("Cancel all requests to elasticsearch");
        for (Iterator<Request> iterator = requests.iterator(); iterator.hasNext(); )
        {
            Request request = iterator.next();
            request.cancel();
            iterator.remove();
        }
    }
}
