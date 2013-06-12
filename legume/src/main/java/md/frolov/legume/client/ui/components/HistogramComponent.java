package md.frolov.legume.client.ui.components;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.elastic.model.reply.Facet;
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
    private static final long MAXIMUM_STEPS = 2000; //TODO constraint to client width?

    interface HistogramComponentUiBinder extends UiBinder<Widget, HistogramComponent>
    {
    }

    private static HistogramComponentUiBinder binder = GWT.create(HistogramComponentUiBinder.class);

    private static DateTimeFormat DTF = DateTimeFormat.getFormat("dd/MM HH:mm");

    @UiField(provided = true)
    SimplePlot plot;
    @UiField
    ResizeLayoutPanel resizePanel;
    @UiField
    FlowPanel loading;
    @UiField
    FlowPanel error;
    @UiField
    Button zoomIn;
    @UiField
    Button zoomOut;
    @UiField
    FlowPanel controls;
    @UiField
    Button downloadImage;
    @UiField
    Button trackPosition;

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();
    private ElasticSearchService elasticSearchService = WidgetInjector.INSTANCE.elasticSearchService();

    private Search activeSearch;
    private boolean inprocess = false;

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
        plotOptions.setCrosshairOptions(CrosshairOptions.create().setColor("hsl(210,60%,70%)").setLineWidth(1).setMode(CrosshairOptions.Mode.X));

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

                /*
                plot.clearSelection(true);
                plot.setVisible(false);
                controls.setVisible(false);
                loading.setVisible(true);
                */
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

    private void updateHistogramWithData(ElasticSearchReply response, final Date from, final Date to, final HistogramInterval interval)
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

        long min;
        if(from!=null){
            min = from.getTime();
        } else {
            min = Iterables.getFirst(values.keySet(),null).longValue();
        }
        long max;
        if(to!=null) {
            max = to.getTime();
        } else {
            max = Iterables.getLast(values.keySet()).longValue();
        }
        plot.getPlotOptions().setXAxesOptions(AxesOptions.create().addAxisOptions(TimeSeriesAxisOptions.create()
                .setTickColor("#eee").setReserveSpace(true).setMinimum(min).setMaximum(max)
                .setTimeZone("browser").setTimeFormat(interval.getDateTimeFormat())
        ));

        loading.setVisible(false);
        plot.setVisible(true);
        controls.setVisible(true);
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
        requestHistogram(event.getSearchQuery());
    }

    @UiHandler("resizePanel")
    public void onPanelResize(final ResizeEvent event)
    {
        plot.setWidth(event.getWidth()-100);
        plot.redraw();
    }

    private void requestHistogram(Search searchQuery) {
        if(inprocess) {
            return;
        }

        inprocess = true;
        plot.setVisible(false);
        controls.setVisible(false);
        error.setVisible(true);
        loading.setVisible(true);

        activeSearch = searchQuery;
        HistogramRequest request = new HistogramRequest();
        final Date from = searchQuery.getFromDate();
        final Date to = searchQuery.getToDate();
        final HistogramInterval interval = getInterval(searchQuery.getFromDate(), searchQuery.getToDate());
        request.setFromDate(from);
        request.setToDate(to);
        request.setInterval(interval);
        request.setQuery(searchQuery.getQuery());

        WidgetInjector.INSTANCE.elasticSearchService().query(request, new AsyncCallback<ElasticSearchReply>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                loading.setVisible(false);
                error.setVisible(true);
                inprocess = false;
            }

            @Override
            public void onSuccess(final ElasticSearchReply result)
            {
                updateHistogramWithData(result, from, to, interval);
                inprocess = false;
            }
        }, ElasticSearchReply.class);
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
        if(!trackPosition.isToggled()) {
            return;
        }

        Date selectionDate = event.getDate();
        Date fromDate = activeSearch.getFromDate();
        Date toDate = activeSearch.getToDate();
        boolean update = false;

        if(fromDate!=null && selectionDate.getTime()>toDate.getTime()) {
            update = true;
            long alltime = toDate.getTime() - fromDate.getTime();
            toDate = new Date(selectionDate.getTime() + alltime / 2);
            Date now = new Date();
            if(toDate.getTime()>now.getTime()) {
                toDate = now;
            }
            fromDate = new Date(toDate.getTime()-alltime);
        } else if(fromDate != null && selectionDate.getTime()<fromDate.getTime()) {
            update = true;
            long alltime = toDate.getTime() - fromDate.getTime();
            fromDate = new Date(fromDate.getTime()-alltime/2);
            toDate = new Date(toDate.getTime()-alltime/2);
        }

        if(update) {
            Search search = activeSearch.clone();
            search.setFromDate(fromDate);
            search.setToDate(toDate);
            requestHistogram(search);
        } else {
            plot.lockCrosshair(PlotPosition.of(event.getDate().getTime(),0));
        }
    }

    @UiHandler("zoomIn")
    public void onZoomIn(final ClickEvent event)
    {
        long from = plot.getPlotOptions().getXAxisOptions().getMinimum().longValue();
        long to = plot.getPlotOptions().getXAxisOptions().getMaximum().longValue();

        if(to - from <100){
            return;
        }

        long allTime = to - from;
        Date fromDate = new Date(from + allTime/3);
        Date toDate = new Date(to - allTime/3);

        Search search = activeSearch.clone();
        search.setFromDate(fromDate);
        search.setToDate(toDate);
        requestHistogram(search);
    }

    @UiHandler("zoomOut")
    public void onZoomOut(final ClickEvent event)
    {
        Date from = activeSearch.getFromDate();
        Date to = activeSearch.getToDate();
        Date now = new Date();
        if(from == null) {
            return;
        }
        if(to == null) {
            to = now;
        }

        long allTime = to.getTime() - from.getTime();
        from = new Date(from.getTime() - allTime/2);
        to = new Date(to.getTime() + allTime/2);

        if(to.getTime()>now.getTime()) {
            from = new Date(from.getTime() - (to.getTime() - now.getTime()));
            to = now;
        }
        Search search = activeSearch.clone();
        search.setFromDate(from);
        search.setToDate(to);
        requestHistogram(search);
    }

    @UiHandler("downloadImage")
    public void onDownloadImage(final ClickEvent event)
    {
        if(plot.isExportAsImageEnabled()) {
            plot.saveAsImage();
        } else {
            Window.alert("Sorry. This is not supported in your browser");
        }
    }
}