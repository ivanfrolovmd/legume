package md.frolov.legume.client.activities.stream;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.Application;
import md.frolov.legume.client.Constants;
import md.frolov.legume.client.activities.SearchActivity;
import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.SearchRequest;
import md.frolov.legume.client.elastic.api.SearchResponse;
import md.frolov.legume.client.events.FocusOnDateEvent;
import md.frolov.legume.client.events.FocusOnDateEventHandler;
import md.frolov.legume.client.events.ScrollableStateChangedEvent;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.events.UpdateSearchQueryHandler;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.model.Search;
import md.frolov.legume.client.service.ConfigurationService;

public class StreamActivity extends SearchActivity implements StreamView.Presenter, FocusOnDateEventHandler, UpdateSearchQueryHandler
{
    private static final Logger LOG = Logger.getLogger("StreamActivity");
    private static final int DEFAULT_QUERY_SIZE = WidgetInjector.INSTANCE.configurationService().getInt(Constants.PAGE_SIZE);

    @Inject
    private StreamView streamView;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ElasticSearchService elasticSearchService;

    @Inject
    private PlaceController placeController;

    @Inject
    private Application application;

    private EventBus eventBus;

    private StreamPlace place;
    private long newestDate;
    private long oldestDate;

    /** query to scroll upwards */
    private SearchRequest upwardsQuery;
    /** query to scroll downwards */
    private SearchRequest downwardsQuery;

    private boolean finished = false;

    private boolean initialBottomFinished = false;
    private boolean isInitialUpFinished = false;
    private int totalFound = 0;

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(streamView);
        streamView.setPresenter(this);
        place = (StreamPlace) placeController.getWhere();
        this.eventBus = eventBus;

        eventBus.addHandler(FocusOnDateEvent.TYPE, this);
        eventBus.addHandler(UpdateSearchQuery.TYPE, this);

        initQueries(place.getSearch());

        startRequest();
    }

    private void startRequest() {
        streamView.showLoading();

        requestMoreResults(false, false);
        if(place.getSearch().getToDate()==0 || place.getSearch().getFocusDate() != place.getSearch().getFromDate()) {
            requestMoreResults(true, false);
        } else {
            isInitialUpFinished = true;
        }
    }

    private void initQueries(Search search)
    {
        long from = search.getRealFromDate();
        long to = search.getRealToDate();
        long focus = search.getRealFocusDate();
        downwardsQuery = new SearchRequest(new Search(search.getQuery(), focus, to, focus), true, 0, DEFAULT_QUERY_SIZE);
        upwardsQuery = new SearchRequest(new Search(search.getQuery(), from, focus, focus), false, 0, DEFAULT_QUERY_SIZE);
        oldestDate = focus;
        newestDate = focus;
    }

    @Override
    public void requestMoreResults(final boolean upwards, final boolean force)
    {
        if(force && upwards) {
            upwardsQuery.getSearch().setFromDate(0);
        }
        if(force && !upwards) {
            downwardsQuery.getSearch().setToDate(0);
        }
        final SearchRequest query = upwards ? upwardsQuery : downwardsQuery;

        streamView.showLoading(upwards);
        elasticSearchService.query(query, new Callback<SearchRequest, SearchResponse>()
        {
            @Override
            public void onFailure(final Throwable exception)
            {
                if (finished)
                {
                    return;
                }
                LOG.log(Level.SEVERE, "Can't fetch results", exception);
                checkInitialError(upwards);
            }

            @Override
            public void onSuccess(final SearchRequest request, final SearchResponse response)
            {
                if (finished)
                {
                    return;
                }
                request.setFrom(request.getFrom() + response.getHits().size());
                if(response.getHits().size()>0) {
                    long date = Iterables.getLast(response.getHits()).getLogEvent().getTimestamp().getTime();
                    if(upwards) {
                        oldestDate = date;
                    } else {
                        newestDate = date;
                    }
                    totalFound += response.getHits().size();
                }

                LOG.fine("Got reply");
                streamView.handleNewHits(upwards, request, response);
            }
        });
    }

    private void checkInitialError(boolean upwards) {
        if(initialBottomFinished && isInitialUpFinished) {
            streamView.showError(upwards);
        } else {
            streamView.showError();
        }
    }

    @Override
    public void checkInitialRequests(boolean upwards) {
        if(upwards) {
            isInitialUpFinished = true;
        } else {
            initialBottomFinished = true;
        }

        if(initialBottomFinished && isInitialUpFinished) {
            if(totalFound > 0) {
                streamView.showResults();
            } else {
                streamView.showNothingFound();
            }
        }
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

    private void stop()
    {
        finished = true;
        elasticSearchService.cancelAllRequests();
        eventBus.fireEvent(new ScrollableStateChangedEvent(false));
    }

    @Override
    public void onFocusOnDate(final FocusOnDateEvent event)
    {
        long focusDate = event.getFocusDate();
        if(focusDate>= oldestDate && focusDate<=newestDate) {
            streamView.focusOnDate(focusDate);
        }
        Search search = application.getCurrentSearch().clone();
        search.setFocusDate(focusDate);
        eventBus.fireEvent(new UpdateSearchQuery(search));
    }

    @Override
    public boolean canActivityBeReused(final SearchPlace newPlace)
    {
        if(!(newPlace instanceof StreamPlace)) {
            return false;
        }
        return Strings.nullToEmpty(place.getSearch().getQuery()).equals(Strings.nullToEmpty(newPlace.getSearch().getQuery()))
                && newPlace.getSearch().getFocusDate() >= oldestDate
                && newPlace.getSearch().getFocusDate() <= newestDate;
    }

    @Override
    public void tryAgain()
    {
        isInitialUpFinished = false;
        isInitialUpFinished = false;
        downwardsQuery.setFrom(0);
        upwardsQuery.setFrom(0);

        startRequest();
    }

    @Override
    public void onUpdateSearchQuery(final UpdateSearchQuery event)
    {
        long from = event.getSearchQuery().getRealFromDate();
        long to = event.getSearchQuery().getRealToDate();
        downwardsQuery.getSearch().setToDate(to);
        upwardsQuery.getSearch().setFromDate(from);
    }
}
