package md.frolov.legume.client.elastic.api;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface Callback<REQUEST extends ESRequest, RESPONSE extends ESResponse>
{
    void onFailure(Throwable exception);
    void onSuccess(REQUEST query, RESPONSE response);
}
