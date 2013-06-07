package md.frolov.legume.client.ui.components;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gflot.client.Axis;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.PlotSelectionArea;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.event.PlotSelectedListener;
import com.googlecode.gflot.client.options.*;
import com.googlecode.gflot.client.options.side.IntegerSideOptions;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.model.response.Facet;
import md.frolov.legume.client.elastic.query.Search;
import md.frolov.legume.client.events.HistogramUpdatedEvent;
import md.frolov.legume.client.events.HistogramUpdatedEventHandler;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramComponent extends Composite implements HistogramUpdatedEventHandler
{
    interface HistogramComponentUiBinder extends UiBinder<Widget, HistogramComponent>
    {
    }

    private static HistogramComponentUiBinder binder = GWT.create(HistogramComponentUiBinder.class);

    private static DateTimeFormat DTF = DateTimeFormat.getFormat("HH:mm");

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();

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

        eventBus.addHandler(HistogramUpdatedEvent.TYPE, this);
    }

    private void initPlot()
    {
        PlotOptions plotOptions = PlotOptions.create();

        // add tick formatter to the options
        plotOptions.addXAxisOptions(AxisOptions.create().setTickColor("#eee").setLabelPadding(0)
                /*
                .setTicks(new AbstractAxisOptions.TickGenerator()
                {
                    @Override
                    public Tick[] generate(final Axis axis)
                    {
                        Date current = new Date(axis.getMinimumValue().longValue());
                        Date to = new Date(axis.getMaximumValue().longValue());
                        if (current.compareTo(to) > 0)
                        {
                            return new Tick[0];
                        }

                        current.setSeconds(0);
                        current.setMinutes(0);;

                        List<Tick> ticks =Lists.newArrayList();
                        while(current.compareTo(to)<0) {
                            current.setHours(current.getHours()+1);
                            ticks.add(Tick.of((double) current.getTime(), DTF.format(current)));
                        }

                        return ticks.toArray(new Tick[0]);
                    }
                })
                */
                .setTickFormatter(new TickFormatter()
                {
                    @Override
                    public String formatTickValue(double tickValue, Axis axis)
                    {
                        if (tickValue > 0)
                        {
                            return DTF.format(new Date((long) tickValue));
                        }
                        else
                        {
                            return "";
                        }
                    }
                })
        );

        plotOptions.addYAxisOptions(AxisOptions.create().setTickColor("#fafafa"));

        //Styling
        plotOptions.setGridOptions(GridOptions.create().setBorderWidth(IntegerSideOptions.of(0,0,1,0)).setBorderColor("#999")
                .setClickable(true).setAutoHighlight(false));
        plotOptions.setSelectionOptions(SelectionOptions.create().setMode(SelectionOptions.SelectionMode.X).setColor("#ccc"));
        plotOptions.setGlobalSeriesOptions(GlobalSeriesOptions.create().setShadowSize(0).setLineSeriesOptions(
                LineSeriesOptions.create().setFill(true).setSteps(true).setZero(true).setLineWidth(1)
        ));

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

    @Override
    public void onHistogramUpdated(final HistogramUpdatedEvent event)
    {
        Facet facet = event.getSearchResponse().getFacets().values().iterator().next();
        List<Facet.Range> ranges = facet.getRanges();

        plot.getModel().removeAllSeries();
        SeriesHandler handler = plot.getModel().addSeries(Series.of("","#999"));

        for (Facet.Range range : ranges)
        {
            handler.add(DataPoint.of(range.getFrom(), range.getCount()));
        }

        loading.setVisible(false);
        plot.setVisible(true);
        plot.redraw(true);
    }

    @UiHandler("resizePanel")
    public void onPanelResize(final ResizeEvent event)
    {
        plot.setWidth(event.getWidth()-100);
        plot.redraw();
    }
}