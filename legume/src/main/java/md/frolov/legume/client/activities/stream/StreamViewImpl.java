package md.frolov.legume.client.activities.stream;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.elastic.model.SearchHit;
import md.frolov.legume.client.elastic.model.SearchHits;
import md.frolov.legume.client.events.SearchResultsReceivedEvent;
import md.frolov.legume.client.events.SearchResultsReceivedEventHandler;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.ui.components.LogEventComponent;
import md.frolov.legume.client.util.IteratorIncrementalTask;

public class StreamViewImpl extends Composite implements StreamView, SearchResultsReceivedEventHandler
{

    private static final int SCROLL_THRESHOLD = 3000; //TODO configure scroll threshold

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
    HorizontalPanel topNoMoreResults;
    @UiField
    HorizontalPanel bottomNoMoreResults;
    @UiField
    Button bottomTryAgain;
    @UiField
    Button topTryAgain;

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();
    private Presenter presenter;
    private boolean isRendering;

    interface StreamViewImplUiBinder extends UiBinder<Widget, StreamViewImpl>
    {
    }

    public StreamViewImpl()
    {
        initWidget(uiBinder.createAndBindUi(this));
        eventBus.addHandler(SearchResultsReceivedEvent.TYPE, this);
    }

    @Override
    public void setPresenter(final Presenter presenter)
    {
        this.presenter = presenter;
        container.clear();
        ids.clear();
    }

    @Override
    public void onSearchResultsReceived(final SearchResultsReceivedEvent event)
    {
        SearchHits hits = event.getSearchResponse().getHits();
//        resultsPanel.setVisible(false);
        nothingFound.setVisible(false);

        if (hits.getTotal() == 0)
        {
            handleNothingFound();
        }
        else
        {
            if (hits.getHits().isEmpty())
            {
                handleNoMoreResults(event.isTop());
            }
            else
            {
                handleFound(hits, event.isTop());
            }
        }
    }

    private void handleNothingFound()
    {
        nothingFound.setVisible(true);
    }

    private void handleFound(final SearchHits hits, final boolean top)
    {
        isRendering=true;
        Scheduler.get().scheduleIncremental(new IteratorIncrementalTask<SearchHit>(hits.getHits()) {
            private FlowPanel panel;

            @Override
            public void beforeAll()
            {
                panel = new FlowPanel();
            }

            @Override
            public void iterate(final SearchHit hit)
            {
                //guard against duplicates
                String id = hit.getId();
                if (!ids.contains(id))
                {
                    ids.add(id);

                    //add log message
                    LogEventComponent logEventComponent = new LogEventComponent(hit.getId(), hit.getLogEvent());
                    if (top)
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
                if (top)
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
        }
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

        if (toTop < SCROLL_THRESHOLD && !topNoMoreResults.isVisible())
        {
            requestMoreTop();
        }
        if (toBottom < SCROLL_THRESHOLD && !bottomNoMoreResults.isVisible())
        {
            requestMoreBottom();
        }
    }

    private void requestMoreTop()
    {
        if (topLoading.isVisible() || isRendering)
        {
            return;
        }
        topLoading.setVisible(true);
        topNoMoreResults.setVisible(false);
        presenter.requestMoreResults(true);
    }

    private void requestMoreBottom()
    {
        if (bottomLoading.isVisible() || isRendering)
        {
            return;
        }
        bottomLoading.setVisible(true);
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
}

