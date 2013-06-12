package md.frolov.legume.client.activities.stream;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.elastic.query.Search;
import md.frolov.legume.client.events.SearchFinishedEvent;
import md.frolov.legume.client.events.SearchInProgressEvent;
import md.frolov.legume.client.events.SearchResultsReceivedEvent;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.service.ConfigurationService;

public class StreamActivity extends AbstractActivity implements StreamView.Presenter
{
    private static final Logger LOG = Logger.getLogger("StreamActivity");

    @Inject
    private StreamView streamView;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ElasticSearchService elasticSearchService;

    @Inject
    private PlaceController placeController;

    private EventBus eventBus;

    private StreamPlace place;

    /** reference query, requested initially */
    private Search activeSearchQuery;
    /** query to scroll upwards */
    private Search upwardsQuery;
    /** query to scroll downwards */
    private Search downwardsQuery;

    private boolean finished = false;

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(streamView);
        streamView.setPresenter(this);
        place = (StreamPlace) placeController.getWhere();
        this.eventBus = eventBus;

        initQueries(place.getQuery());
        eventBus.fireEvent(new UpdateSearchQuery(activeSearchQuery));
        boolean isUpwards = isUpwardsDirection();

        requestMoreResults(isUpwards);
    }

    private void initQueries(Search requestedSearchQuery)
    {
        activeSearchQuery = requestedSearchQuery;

        Date fromDate = activeSearchQuery.getFromDate();
        Date toDate = activeSearchQuery.getToDate();
        Date focusDate = activeSearchQuery.getFocusDate();

        if (fromDate == null && toDate == null && focusDate == null)
        {
            //setting search to "all time up to now, focus on now"
            focusDate = new Date();
            toDate = focusDate;
        }
        if (focusDate == null)
        {
            focusDate = fromDate != null ? fromDate : toDate;
        }

        activeSearchQuery.setFromDate(fromDate);
        activeSearchQuery.setToDate(toDate);
        activeSearchQuery.setFocusDate(focusDate);

        downwardsQuery = activeSearchQuery.clone();
        downwardsQuery.setFromDate(focusDate);
        downwardsQuery.setToDate(null);

        upwardsQuery = activeSearchQuery.clone();
        upwardsQuery.setToDate(focusDate);
        upwardsQuery.setFromDate(null);
        upwardsQuery.reverseSortOrder();
    }

    private boolean isUpwardsDirection()
    {
        return activeSearchQuery.getFocusDate().equals(activeSearchQuery.getToDate());
    }

    @Override
    public void requestMoreResults(final boolean upwards)
    {
        final Search query = upwards ? upwardsQuery : downwardsQuery;
        eventBus.fireEvent(new SearchInProgressEvent(upwards));
        elasticSearchService.query(query, new AsyncCallback<ElasticSearchReply>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                if (finished)
                {
                    return;
                }
                LOG.log(Level.SEVERE, "Can't fetch results", caught);
                eventBus.fireEvent(new SearchFinishedEvent(upwards));
            }

            @Override
            public void onSuccess(final ElasticSearchReply result)
            {
                if (finished)
                {
                    return;
                }
                query.setFrom(query.getFrom() + result.getHits().getHits().size());

                LOG.fine("Got reply");
                eventBus.fireEvent(new SearchResultsReceivedEvent(query, result, upwards));
                eventBus.fireEvent(new SearchFinishedEvent(upwards));
            }
        }, ElasticSearchReply.class);
    }

    @Override
    public void onStop()
    {
        stop();
    }

    @Override
    public void onCancel()
    {
        stop();
    }

    private void stop() {
        finished = true;
        elasticSearchService.cancelAllRequests();
    }
}
