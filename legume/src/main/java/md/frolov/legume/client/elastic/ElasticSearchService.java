package md.frolov.legume.client.elastic;

import com.google.gwt.user.client.rpc.AsyncCallback;

import md.frolov.legume.client.elastic.model.request.ElasticSearchRequest;
import md.frolov.legume.client.elastic.query.Query;

public interface ElasticSearchService {

    <T> void query(Query query, AsyncCallback<T> callback, Class<T> clazz);
    <T> void queryRaw(String uri, ElasticSearchRequest request, AsyncCallback<T> callback, Class<T> clazz);

    void cancelAllRequests();

}
