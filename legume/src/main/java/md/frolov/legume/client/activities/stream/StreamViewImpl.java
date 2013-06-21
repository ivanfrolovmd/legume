package md.frolov.legume.client.activities.stream;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.Application;
import md.frolov.legume.client.elastic.model.reply.SearchHit;
import md.frolov.legume.client.events.SearchFinishedEvent;
import md.frolov.legume.client.events.SearchFinishedEventHandler;
import md.frolov.legume.client.events.SearchInProgressEvent;
import md.frolov.legume.client.events.SearchInProgressEventHandler;
import md.frolov.legume.client.events.SearchResultsReceivedEvent;
import md.frolov.legume.client.events.SearchResultsReceivedEventHandler;
import md.frolov.legume.client.model.Search;
import md.frolov.legume.client.service.ColorizeService;
import md.frolov.legume.client.ui.EventFlowPanel;
import md.frolov.legume.client.ui.components.LogEventComponent;
import md.frolov.legume.client.util.IteratorIncrementalTask;

public class StreamViewImpl extends Composite implements StreamView, SearchResultsReceivedEventHandler, SearchInProgressEventHandler, SearchFinishedEventHandler
{
    private static final Logger LOG = Logger.getLogger("StreamView");

    private static StreamViewImplUiBinder uiBinder = GWT.create(StreamViewImplUiBinder.class);

    private final Set ids = Sets.newHashSet();

    @UiField
    FlowPanel container;
    @UiField
    ScrollPanel scrollContainer;
    @UiField
    FlowPanel topLoading;
    @UiField
    FlowPanel bottomLoading;

    @UiField
    FlowPanel nothingFound;
    @UiField
    FlowPanel resultsPanel;
    @UiField
    FlowPanel topNoMoreResults;
    @UiField
    FlowPanel bottomNoMoreResults;
    @UiField
    Button bottomTryAgain;
    @UiField
    Button topTryAgain;
    @UiField
    Button bottomAutoFetch;

    @Inject
    @Named("scrollThreashold")
    private Integer scrollThreashold = 50;

    @Inject
    private ColorizeService colorizeService;
    @Inject
    private PlaceController placeController;
    @Inject
    private Application application;

    @Inject
    private EventBus eventBus;

    private Presenter presenter;
    private boolean isRendering;
    private boolean initial;

    interface StreamViewImplUiBinder extends UiBinder<Widget, StreamViewImpl>
    {
    }

    @Inject
    public StreamViewImpl(EventBus eventBus)
    {
        initWidget(uiBinder.createAndBindUi(this));
        eventBus.addHandler(SearchResultsReceivedEvent.TYPE, this);
        eventBus.addHandler(SearchInProgressEvent.TYPE, this);
        eventBus.addHandler(SearchFinishedEvent.TYPE, this);
    }

    @Override
    public void setPresenter(final Presenter presenter)
    {
        this.presenter = presenter;
        container.clear();
        ids.clear();
        initial = true;
    }

    @Override
    public void onSearchResultsReceived(final SearchResultsReceivedEvent event)
    {
//        resultsPanel.setVisible(false);
        nothingFound.setVisible(false);

        if(initial) {
            initial = false;
            requestMoreTop();
            requestMoreBottom();
        }

        Scheduler.get().scheduleEntry(new Scheduler.RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                if(!isRendering) {
                    handleFound(event);
                    return false; // stop repeating the command
                } else {
                    return true; //wait until the rendering is finished
                }
            }
        });
    }

    private void handleNothingFound()
    {
        nothingFound.setVisible(true);
    }

    private void handleFound(final SearchResultsReceivedEvent event)
    {
        isRendering=true;

        final List<SearchHit> hits = event.getSearchResponse().getHits();
        final boolean isFullFetch = hits.size() == event.getSearchRequest().getSize();
        final boolean upwards = event.isUpwards();

        Scheduler.get().scheduleIncremental(new IteratorIncrementalTask<SearchHit>(hits) {
            private EventFlowPanel panel;
            private int cnt = 0;

            @Override
            public void beforeAll()
            {
                panel = new EventFlowPanel();
                SearchHit firstHit = Iterables.getFirst(event.getSearchResponse().getHits(), null);
                if(firstHit!=null) {
                    final long focusDate = firstHit.getLogEvent().getTimestamp().getTime();
                    panel.addMouseOverHandler(new MouseOverHandler()
                    {
                        @Override
                        public void onMouseOver(final MouseOverEvent event)
                        {
                            Search search = application.getCurrentSearch().clone();
                            search.setFocusDate(focusDate);
                            placeController.goTo(new StreamPlace(search));
                        }
                    });
                }
            }

            @Override
            public void iterate(final SearchHit hit)
            {
                //guard against duplicates
                String id = hit.getId();
                if (!ids.contains(id))
                {
                    ids.add(id);
                    cnt++;

                    //add log message
                    LogEventComponent logEventComponent = new LogEventComponent(hit.getId(), hit.getLogEvent());
                    if (upwards)
                    {
                        panel.insert(logEventComponent, 0);
                    }
                    else
                    {
                        panel.add(logEventComponent);
                    }
                }
            }

            @Override
            public void afterAll()
            {
//                 resultsPanel.setVisible(true);
                if (upwards)
                {
                    int scrollToBottom = scrollContainer.getMaximumVerticalScrollPosition() - scrollContainer.getVerticalScrollPosition();
                    container.insert(panel, 0);
                    int scrollPosition = scrollContainer.getMaximumVerticalScrollPosition() - scrollToBottom;
                    scrollContainer.setVerticalScrollPosition(scrollPosition);
                    topLoading.setVisible(false);
                }
                else
                {
                    container.add(panel);
                    bottomLoading.setVisible(false);
                }
                isRendering = false;

                if(cnt == 0) {
                    if(isFullFetch) {
                        requestMore(upwards);
                    } else {
                        handleNoMoreResults(upwards);
                    }
                }

                colorizeService.refresh();
            }
        });
    }

    private void handleNoMoreResults(boolean top)
    {
        if (top)
        {
            topNoMoreResults.setVisible(true);
            topLoading.setVisible(false);
        }
        else
        {
            bottomNoMoreResults.setVisible(true);
            bottomLoading.setVisible(false);
            if(bottomAutoFetch.isToggled()) {
                startAutoFetchTimer();
            }
        }
    }

    private void startAutoFetchTimer() {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                requestMoreBottom();
                return false; //don't repeat
            }
        }, 30000); //TODO externalize
    }

    @UiHandler("scrollContainer")
    public void onScroll(final ScrollEvent event)
    {
        int toTop = scrollContainer.getVerticalScrollPosition() - scrollContainer.getMinimumVerticalScrollPosition();
        int toBottom = scrollContainer.getMaximumVerticalScrollPosition() - scrollContainer.getVerticalScrollPosition();

        if (toTop == 0 && toBottom == 0)
        {
            return;
        }

        if (toTop < scrollThreashold && !topNoMoreResults.isVisible())
        {
            requestMoreTop();
        }
        if (toBottom < scrollThreashold && !bottomNoMoreResults.isVisible())
        {
            requestMoreBottom();
        }
    }

    private void requestMore(boolean upwards) {
        if(upwards) {
            requestMoreTop();
        } else {
            requestMoreBottom();
        }
    }

    private void requestMoreTop()
    {
        if (topLoading.isVisible() || isRendering)
        {
            return;
        }
        LOG.info("Request top");
        topNoMoreResults.setVisible(false);
        presenter.requestMoreResults(true);
    }

    private void requestMoreBottom()
    {
        if (bottomLoading.isVisible() || isRendering)
        {
            return;
        }
        LOG.info("Request bottom");
        bottomNoMoreResults.setVisible(false);
        presenter.requestMoreResults(false);
    }

    @UiHandler("topTryAgain")
    public void onTopAgainClick(final ClickEvent event)
    {
        requestMoreTop();
    }

    @UiHandler("bottomTryAgain")
    public void onBottomTryAgain(final ClickEvent event)
    {
        requestMoreBottom();
    }

    @UiHandler("bottomAutoFetch")
    public void handleClick(final ClickEvent event)
    {
        if(!bottomAutoFetch.isToggled()) {
            startAutoFetchTimer();
        }
    }

    @Override
    public void onSearchFinished(final SearchFinishedEvent event)
    {
        if(event.isUpwards()) {
            topLoading.setVisible(false);
        } else {
            bottomLoading.setVisible(false);
        }
    }

    @Override
    public void onSearchInProgress(final SearchInProgressEvent event)
    {
        if(event.isUpwards()) {
            topLoading.setVisible(true);
        } else {
            bottomLoading.setVisible(true);
        }
    }

    @Override
    public void focusOnDate(final long focusDate)
    {
        //TODO scroll to where appropriate
    }
}

