package md.frolov.legume.client.elastic;

import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.ESRequest;
import md.frolov.legume.client.elastic.api.ESResponse;

public interface ElasticSearchService {
    <REQUEST extends ESRequest<QUERY,REPLY,RESPONSE>, RESPONSE extends ESResponse<REPLY>, QUERY,REPLY> void query(REQUEST request, Callback<REQUEST,RESPONSE> callback);

    void cancelAllRequests();

}
