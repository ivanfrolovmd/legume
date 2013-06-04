package md.frolov.legume.client.elastic.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Sets;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequest;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import md.frolov.legume.client.Constants;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.query.Query;
import md.frolov.legume.client.service.ConfigurationService;

@Singleton
public class ElasticSearchServiceImpl implements ElasticSearchService, Constants {

    private final static Logger LOG = Logger.getLogger("ElasticSearchServiceImpl");

    private final Set<JsonpRequest> requests = Sets.newHashSet();

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ModelFactory modelFactory;

    @Override
    public <T> void query(final Query query, final AsyncCallback<T> callback, final Class<T> clazz) {
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        String url = configurationService.get(ELASTICSEARCH_SERVER) + query.toQueryString();
        requestBuilder.setTimeout(configurationService.getInt(ELASTICSEARCH_TIMEOUT));

        JsonpRequest req = requestBuilder.requestObject(url, new AsyncCallback<JavaScriptObject>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(JavaScriptObject result)
            {
                try
                {
                    T response = AutoBeanCodex.decode(modelFactory, clazz, new JSONObject(result).toString()).as();
                    callback.onSuccess(response);
                }
                catch (Exception e)
                {
                    onFailure(e);
                }
            }
        });
        requests.add(req);
    }

    @Override
    public void cancelAllRequests() {
        LOG.info("Cancel all requests to elasticsearch");
        for (Iterator<JsonpRequest> iterator = requests.iterator(); iterator.hasNext(); )
        {
            JsonpRequest request =  iterator.next();
            request.cancel();
            iterator.remove();
        }
    }
}
