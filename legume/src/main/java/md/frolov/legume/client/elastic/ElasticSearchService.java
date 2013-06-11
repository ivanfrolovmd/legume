package md.frolov.legume.client.elastic;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.ESRequest;
import md.frolov.legume.client.elastic.api.ESResponse;
import md.frolov.legume.client.elastic.model.query.ElasticSearchRequest;
import md.frolov.legume.client.elastic.query.RequestQuery;

public interface ElasticSearchService {

    <T> void query(RequestQuery query, AsyncCallback<T> callback, Class<T> clazz);
    <T> void queryRaw(String uri, RequestBuilder.Method method, ElasticSearchRequest request, AsyncCallback<T> callback, Class<T> clazz);

    <REQUEST extends ESRequest<QUERY,REPLY,RESPONSE>, RESPONSE extends ESResponse<REPLY>, QUERY,REPLY> void query(REQUEST request, Callback<REQUEST,RESPONSE> callback);

    void cancelAllRequests();

}
