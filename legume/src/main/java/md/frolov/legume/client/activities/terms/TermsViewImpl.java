package md.frolov.legume.client.activities.terms;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gflot.client.PieDataPoint;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.GridOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions;
import com.googlecode.gflot.client.options.PlotOptions;

import md.frolov.legume.client.elastic.api.TermsFacetResponse;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsViewImpl extends Composite implements TermsView
{
    interface TermsViewImplUiBinder extends UiBinder<Widget, TermsViewImpl>
    {
    }

    private static TermsViewImplUiBinder binder = GWT.create(TermsViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<Map.Entry<String, Long>> results;
    @UiField(provided = true)
    SimplePlot plot;
    @UiField
    Label totalLabel;
    @UiField
    Label otherLabel;
    @UiField
    Label missingLabel;

    public TermsViewImpl()
    {
        initResultsCellTable();
        initPlot();
        initWidget(binder.createAndBindUi(this));
    }

    private void initPlot()
    {
        final PlotOptions plotOptions = PlotOptions.create();

        // activate the pie
        plotOptions.setGlobalSeriesOptions(
                GlobalSeriesOptions.create().setPieSeriesOptions(
                        PieSeriesOptions.create().setShow(true).setRadius(1).setInnerRadius(0.2).setLabel(
                                PieSeriesOptions.Label.create().setShow(false)
                        )
                )
        );
        plotOptions.setLegendOptions(LegendOptions.create().setShow(false));
        plotOptions.setGridOptions(GridOptions.create().setHoverable(false));

        // create the plot
        plot = new SimplePlot(plotOptions);
    }

    private void initResultsCellTable()
    {
        results = new CellTable<Map.Entry<String, Long>>();
        results.addColumn(new TextColumn<Map.Entry<String, Long>>()
        {
            @Override
            public String getValue(final Map.Entry<String, Long> object)
            {
                return object.getKey();
            }
        }, "Term");

        //TODO add filter action

        results.addColumn(new TextColumn<Map.Entry<String, Long>>()
        {
            @Override
            public String getValue(final Map.Entry<String, Long> object)
            {
                return object.getValue().toString();
            }
        }, "Count");
    }

    @Override
    public void handleResults(final TermsFacetResponse response)
    {
        results.setRowData(Lists.newArrayList(response.getTerms().entrySet()));

        plot.getModel().removeAllSeries();
        PlotModel model = plot.getModel();
        for (Map.Entry<String, Long> entry : response.getTerms().entrySet())
        {
            SeriesHandler handler = model.addSeries(Series.create()); //TODO set color?
            handler.add(PieDataPoint.of(entry.getValue()));
        }
        plot.redraw();

        totalLabel.setText("Total: " + response.getTotal());
        missingLabel.setText("Missing: " + response.getMissing());
        otherLabel.setText("Other: " + response.getOther());
    }
}