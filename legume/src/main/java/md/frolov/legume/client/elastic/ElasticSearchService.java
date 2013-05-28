package md.frolov.legume.client.elastic;

import com.google.gwt.user.client.rpc.AsyncCallback;

import md.frolov.legume.client.elastic.model.Query;

public interface ElasticSearchService {

    <T> void query(final Query query, final AsyncCallback<T> callback, final Class<T> clazz);

}
