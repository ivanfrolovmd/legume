package md.frolov.legume.client.ui.components;

import java.util.Date;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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

import md.frolov.legume.client.Application;
import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.HistogramRequest;
import md.frolov.legume.client.elastic.api.HistogramResponse;
import md.frolov.legume.client.events.LogMessageHoverEvent;
import md.frolov.legume.client.events.LogMessageHoverEventHandler;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.events.UpdateSearchQueryHandler;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramComponent extends Composite implements UpdateSearchQueryHandler, LogMessageHoverEventHandler
{
    private static final int MAXIMUM_STEPS = 2000; //TODO constraint to client width?

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
    private Application application = WidgetInjector.INSTANCE.application();

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
                long from = area.getX().getFrom().longValue();
                long to = area.getX().getTo().longValue();

                Search search = application.getCurrentSearch().clone();
                search.setFromDate(from);
                search.setToDate(to);
                search.setFocusDate(from);
                WidgetInjector.INSTANCE.placeController().goTo(new StreamPlace(search)); //TODO change this. It might be useful to zoom in/out when in 'terms' activity
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

    private void updateHistogramWithData(HistogramResponse response)
    {
        plot.getModel().removeAllSeries();
        SeriesHandler handler = plot.getModel().addSeries(Series.of("","#999"));

        for (Map.Entry<Long, Long> entry : response.getDateValueMap().entrySet())
        {
            handler.add(DataPoint.of(entry.getKey(),entry.getValue()));
        }

        plot.getPlotOptions().setXAxesOptions(AxesOptions.create().addAxisOptions(TimeSeriesAxisOptions.create()
                .setTickColor("#eee").setReserveSpace(true)
                .setTimeZone("browser").setTimeFormat(response.getInterval().getDateTimeFormat())
        ));

        loading.setVisible(false);
        plot.setVisible(true);
        controls.setVisible(true);
        plot.redraw(true);

        plot.clearCrosshair();
        plot.lockCrosshair();
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

    private void requestHistogram(Search search) {
        if(inprocess) {
            return;
        }

        inprocess = true;
        plot.setVisible(false);
        controls.setVisible(false);
        error.setVisible(true);
        loading.setVisible(true);

        HistogramRequest request = new HistogramRequest(search, MAXIMUM_STEPS);
        elasticSearchService.query(request, new Callback<HistogramRequest, HistogramResponse>()
        {
            @Override
            public void onFailure(final Throwable exception)
            {
                loading.setVisible(false);
                error.setVisible(true);
                inprocess = false;
            }

            @Override
            public void onSuccess(final HistogramRequest query, final HistogramResponse response)
            {
                updateHistogramWithData(response);
                inprocess = false;
            }
        });
    }


    @Override
    public void onLogMessageHover(final LogMessageHoverEvent event)
    {
        if(!trackPosition.isToggled()) {
            return;
        }

        Search search = application.getCurrentSearch();
        long selectionDate = event.getDate();
        long toDate = search.getToDate();
        if(toDate<0) {
            toDate = new Date().getTime() + toDate;
        }
        long fromDate = search.getFromDate();
        if(fromDate <0) {
            fromDate = toDate + fromDate;
        }
        boolean update = false;

        if(fromDate!=0 && selectionDate>toDate) {
            update = true;
            long alltime = toDate - fromDate;
            toDate = selectionDate + alltime / 2;
            long now = new Date().getTime();
            if(toDate>now) {
                toDate = now;
            }
            fromDate = toDate-alltime;
        } else if(fromDate != 0 && selectionDate<fromDate) {
            update = true;
            long alltime = toDate - fromDate;
            fromDate = fromDate-alltime/2;
            toDate = toDate-alltime/2;
        }

        if(update) {
            Search newSearch = search.clone();
            search.setFromDate(fromDate);
            search.setToDate(toDate);
            requestHistogram(search);
        } else {
            plot.lockCrosshair(PlotPosition.of(event.getDate(),0));
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
        long fromDate = from + allTime/3;
        long toDate = to - allTime/3;

        Search search = application.getCurrentSearch().clone();
        search.setFromDate(fromDate);
        search.setToDate(toDate);
        requestHistogram(search);
    }

    @UiHandler("zoomOut")
    public void onZoomOut(final ClickEvent event)
    {
        Search search = application.getCurrentSearch();
        long from = search.getFromDate();
        long to = search.getToDate();
        long now = new Date().getTime();
        if(from == 0) {
            return;
        }
        if(to == 0) {
            to = now;
        }
        if(to<0) {
            to = now+to;
        }
        if(from<0) {
            from = from+to;
        }

        long allTime = to - from;
        from = from - allTime/2;
        to = to + allTime/2;

        if(to>now) {
            from = from - (to - now);
            to = now;
        }
        Search newSearch = search.clone();
        newSearch.setFromDate(from);
        newSearch.setToDate(to);
        requestHistogram(newSearch);
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