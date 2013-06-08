package md.frolov.legume.client.ui.components;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.PlotSelectionArea;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.event.PlotPosition;
import com.googlecode.gflot.client.event.PlotSelectedListener;
import com.googlecode.gflot.client.options.*;
import com.googlecode.gflot.client.options.side.IntegerSideOptions;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.response.Facet;
import md.frolov.legume.client.elastic.model.response.SearchResponse;
import md.frolov.legume.client.elastic.query.HistogramInterval;
import md.frolov.legume.client.elastic.query.HistogramRequest;
import md.frolov.legume.client.elastic.query.Search;
import md.frolov.legume.client.events.LogMessageHoverEvent;
import md.frolov.legume.client.events.LogMessageHoverEventHandler;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.events.UpdateSearchQueryHandler;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramComponent extends Composite implements UpdateSearchQueryHandler, LogMessageHoverEventHandler
{
    private static final long MAXIMUM_STEPS = 500; //TODO constraint to client width?

    interface HistogramComponentUiBinder extends UiBinder<Widget, HistogramComponent>
    {
    }

    private static HistogramComponentUiBinder binder = GWT.create(HistogramComponentUiBinder.class);

    private static DateTimeFormat DTF = DateTimeFormat.getFormat("dd/MM HH:mm");

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();
    private ElasticSearchService elasticSearchService = WidgetInjector.INSTANCE.elasticSearchService();

    @UiField(provided = true)
    SimplePlot plot;
    @UiField
    ResizeLayoutPanel resizePanel;
    @UiField
    FlowPanel loading;
    @UiField
    FlowPanel error;

    public HistogramComponent()
    {
        initPlot();
        initWidget(binder.createAndBindUi(this));

        eventBus.addHandler(UpdateSearchQuery.TYPE, this);
        eventBus.addHandler(LogMessageHoverEvent.TYPE, this);
    }

    private void initPlot()
    {
        PlotOptions plotOptions = PlotOptions.create();

        // add tick formatter to the options
        plotOptions.addYAxisOptions(AxisOptions.create().setTickColor("#fafafa"));

        //Styling
        plotOptions.setGridOptions(GridOptions.create().setBorderWidth(IntegerSideOptions.of(0, 0, 1, 0)).setBorderColor("#999")
                .setClickable(true).setAutoHighlight(false));
        plotOptions.setSelectionOptions(SelectionOptions.create().setMode(SelectionOptions.SelectionMode.X).setColor("#ccc"));
        plotOptions.setGlobalSeriesOptions(GlobalSeriesOptions.create().setShadowSize(0).setLineSeriesOptions(
                LineSeriesOptions.create().setFill(true).setSteps(true).setZero(true).setLineWidth(1)
        ));

        //Crosshair
        plotOptions.setCrosshairOptions(CrosshairOptions.create().setColor("#999").setLineWidth(1).setMode(CrosshairOptions.Mode.X));

        // create the plot
        plot = new SimplePlot(plotOptions);

        //add listeners
        plot.addSelectedListener(new PlotSelectedListener()
        {
            @Override
            public void onPlotSelected(final PlotSelectionArea area)
            {
                Date from = new Date(area.getX().getFrom().longValue());
                Date to = new Date(area.getX().getTo().longValue());

                Search q = ((StreamPlace)WidgetInjector.INSTANCE.placeController().getWhere()).getQuery();
                q.setFromDate(from);
                q.setToDate(to);
                q.setFocusDate(from);
                WidgetInjector.INSTANCE.placeController().goTo(new StreamPlace(q));

                plot.clearSelection(true);
                plot.setVisible(false);
                loading.setVisible(true);
            }
        });
        /*
        plot.addClickListener(new PlotClickListener()
        {
            @Override
            public void onPlotClick(final Plot plot, final PlotPosition position, final PlotItem item)
            {
                Range xRange = plot.getSelection().getX();
                if (xRange.getFrom() != xRange.getTo())
                {
                    return;
                }
                Date pos = new Date(position.getX().longValue());
                Window.alert("Clicked: " + pos);
            }
        }, false);
        */
    }

    private void updateHistogramWithData(SearchResponse response, final Date from, final Date to, final HistogramInterval interval)
    {
        Facet facet = response.getFacets().values().iterator().next();
        List<Facet.Entry> entries = facet.getEntries();

        plot.getModel().removeAllSeries();
        SeriesHandler handler = plot.getModel().addSeries(Series.of("","#999"));

        Map<Long,Long> values = populateWithNulls(facet.getEntries(), from, to, interval.getTime());
        for (Map.Entry<Long, Long> entry : values.entrySet())
        {
            handler.add(DataPoint.of(entry.getKey(),entry.getValue()));
        }

        plot.getPlotOptions().setXAxesOptions(AxesOptions.create().addAxisOptions(TimeSeriesAxisOptions.create()
                .setTickColor("#eee").setReserveSpace(true)
                .setTimeFormat(interval.getDateTimeFormat())
        ));
        if(from!=null){
            plot.getPlotOptions().getXAxisOptions().setMinimum(from.getTime());
        }
        if(to!=null) {
            plot.getPlotOptions().getXAxisOptions().setMaximum(to.getTime());
        }

        loading.setVisible(false);
        plot.setVisible(true);
        plot.redraw(true);

        plot.clearCrosshair();
        plot.lockCrosshair();
    }

    private Map<Long, Long> populateWithNulls(final List<Facet.Entry> entries, Date from, Date to, final long interval)
    {
        TreeMap<Long, Long> result = Maps.newTreeMap();
        if(entries==null || entries.isEmpty()) {
            result.put(from.getTime(),0l);
            result.put(to.getTime(),0l);
            return result;
        }

        if(from==null) {
            from = entries.get(0).getTime();
        }
        if(to==null) {
            to = new Date();
        }

        for(long t = entries.get(0).getTime().getTime(); t>from.getTime(); t-=interval){
            result.put(t, 0l);
        }
        for(long t=entries.get(0).getTime().getTime(); t<to.getTime(); t+=interval) {
            result.put(t, 0l);
        }
        for (Facet.Entry entry : entries)
        {
            result.put(entry.getTime().getTime(),entry.getCount());
        }
        return result;
    }

    @Override
    public void onUpdateSearchQuery(final UpdateSearchQuery event)
    {
        plot.setVisible(false);
        error.setVisible(true);
        loading.setVisible(true);
        requestHistogram(event.getSearchQuery());
    }

    @UiHandler("resizePanel")
    public void onPanelResize(final ResizeEvent event)
    {
        plot.setWidth(event.getWidth()-100);
        plot.redraw();
    }

    private void requestHistogram(Search searchQuery) {
        HistogramRequest request = new HistogramRequest();
        final Date from = searchQuery.getFromDate();
        final Date to = searchQuery.getToDate();
        final HistogramInterval interval = getInterval(searchQuery.getFromDate(), searchQuery.getToDate());
        request.setFromDate(from);
        request.setToDate(to);
        request.setInterval(interval);
        request.setQuery(searchQuery.getQuery());

        WidgetInjector.INSTANCE.elasticSearchService().query(request, new AsyncCallback<SearchResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                loading.setVisible(false);
                error.setVisible(true);
            }

            @Override
            public void onSuccess(final SearchResponse result)
            {
                updateHistogramWithData(result, from, to, interval);
            }
        }, SearchResponse.class);
    }

    private HistogramInterval getInterval(Date from, Date to) {
        if(from==null) {
            return HistogramInterval.h1;
        }

        if(to==null) {
            to = new Date();
        }

        long allTime = to.getTime()-from.getTime();
        for (HistogramInterval histogramInterval : HistogramInterval.values())
        {
            if(allTime/histogramInterval.getTime()<MAXIMUM_STEPS) {
                return histogramInterval;
            }
        }
        return HistogramInterval.h1;
    }

    @Override
    public void onLogMessageHover(final LogMessageHoverEvent event)
    {
        plot.lockCrosshair(PlotPosition.of(event.getDate().getTime(),0));
    }
}