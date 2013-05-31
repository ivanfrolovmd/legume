package md.frolov.legume.client.elastic.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
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

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ModelFactory modelFactory;

    @Override
    public <T> void query(final Query query, final AsyncCallback<T> callback, final Class<T> clazz) {
        JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
        String url = configurationService.get(ELASTICSEARCH_SERVER) + query.getQueryString();
        requestBuilder.requestObject(url, new AsyncCallback<JavaScriptObject>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(JavaScriptObject result)
            {
                // TODO research performance implications. Compare to "JSO to Bean conversion" method
                T response = AutoBeanCodex.decode(modelFactory, clazz, new JSONObject(result).toString()).as();
                callback.onSuccess(response);
            }
        });
    }
}
