package md.frolov.legume.client.activities.stream;

import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.Query;
import md.frolov.legume.client.elastic.model.SearchResponse;
import md.frolov.legume.client.service.ConfigurationService;

public class StreamActivity extends AbstractActivity
{
    @Inject
    private StreamView streamView;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ElasticSearchService elasticSearchService;

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus)
    {
        panel.setWidget(streamView);

        elasticSearchService.query(new Query("/_search?source={\"query\" : {\"match_all\" : {}}, \"from\" : 0, \"size\" : 10}")
                , new AsyncCallback<SearchResponse>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                Window.alert("Failure " + caught);
            }

            @Override
            public void onSuccess(SearchResponse result)
            {
                Window.alert("success " + result.getHits().getHits().get(0).getLogEvent().getTimestamp());
            }

        }, SearchResponse.class);
    }

}
