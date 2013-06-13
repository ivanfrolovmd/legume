package md.frolov.legume.client.activities.terms;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
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

    @UiField
    FlexTable results;
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
        initPlot();
        initWidget(binder.createAndBindUi(this));

        initResultsTable();
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

    private void initResultsTable() {
        HTMLTable.ColumnFormatter columnFormatter = results.getColumnFormatter();
        columnFormatter.setWidth(1, "50px");
        columnFormatter.setWidth(2,"40px");
    }

    @Override
    public void handleResults(final TermsFacetResponse response)
    {
        results.removeAllRows();
        results.setText(0,0,"Term");
        results.setText(0,1,"Count");
        results.setText(0,2,"");

        plot.getModel().removeAllSeries();
        PlotModel model = plot.getModel();

        int row=1;
        for (Map.Entry<String, Long> entry : response.getTerms().entrySet())
        {
            //results rable
            results.setText(row, 0, entry.getKey());
            results.setText(row, 1, entry.getValue().toString());
            results.setWidget(row, 2, new Button("Action", IconType.FILTER));
            row++;

            //plot
            SeriesHandler handler = model.addSeries(Series.create()); //TODO set color?
            handler.add(PieDataPoint.of(entry.getValue()));
        }

        results.setText(row,0,"Other");
        results.setText(row,1,String.valueOf(response.getOther()));
        results.setText(row,2,"");
        row++;
        SeriesHandler otherHandler = model.addSeries(Series.create());
        otherHandler.add(PieDataPoint.of(response.getOther()));

        results.setText(row,0,"Missing");
        results.setText(row,1,String.valueOf(response.getMissing()));
        results.setText(row,2,"");
        row++;
        SeriesHandler missingHandler = model.addSeries(Series.create());
        missingHandler.add(PieDataPoint.of(response.getMissing()));

        results.setText(row,0,"Total");
        results.setText(row, 1, String.valueOf(response.getTotal()));
        results.setText(row,2,"");

        plot.redraw();
    }
}