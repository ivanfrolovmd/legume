package md.frolov.legume.client.activities.terms;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
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
import md.frolov.legume.client.ui.controls.FieldActionsDropdown;

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
    @UiField
    FlowPanel loadingPanel;
    @UiField
    FlowPanel errorPanel;
    @UiField
    FlowPanel resultsPanel;
    @UiField
    Button tryAgain;

    private Presenter presenter;

    public TermsViewImpl()
    {
        initPlot();
        initWidget(binder.createAndBindUi(this));

        initResultsTable();
    }

    @Override
    public void setPresenter(final Presenter presenter)
    {
        this.presenter = presenter;
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
        columnFormatter.setWidth(1, "90px");
        columnFormatter.setWidth(2,"30px");
    }

    @Override
    public void handleResults(final String fieldName, final TermsFacetResponse response)
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
            results.setWidget(row, 2, new FieldActionsDropdown(fieldName, entry.getKey(), 0));
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
        results.setText(row, 2, "");

        loadingPanel.setVisible(false);
        errorPanel.setVisible(false);
        resultsPanel.setVisible(true);

        plot.redraw();
    }

    @Override
    public void loading()
    {
        loadingPanel.setVisible(true);
        errorPanel.setVisible(false);
        resultsPanel.setVisible(false);
    }

    @Override
    public void error()
    {
        loadingPanel.setVisible(false);
        errorPanel.setVisible(true);
        resultsPanel.setVisible(false);
    }

    @UiHandler("tryAgain")
    public void onTryAgainClick(final ClickEvent event)
    {
        presenter.tryAgain();
    }
}