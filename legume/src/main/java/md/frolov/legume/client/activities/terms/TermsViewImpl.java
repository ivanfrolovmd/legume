package md.frolov.legume.client.activities.terms;

import java.util.Map;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
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

import md.frolov.legume.client.Application;
import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.api.TermsFacetResponse;
import md.frolov.legume.client.service.ColorizeService;
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
    @UiField
    InlineLabel fieldName;
    @UiField
    FlowPanel nothingFoundPanel;

    @Inject
    private ColorizeService colorizeService;

    @Inject
    private PlaceController placeController;

    @Inject
    private Application application;

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
        this.fieldName.setText(fieldName);

        results.removeAllRows();
        results.setText(0,0,"Term");
        results.setText(0,1,"Count");
        results.setText(0,2,"");

        plot.getModel().removeAllSeries();
        PlotModel model = plot.getModel();

        int row=1;
        for (Map.Entry<String, Long> entry : response.getTerms().entrySet())
        {
            String color;
            if(colorizeService.isFieldColorizable(fieldName)) {
                color = colorizeService.getCssColor(fieldName, entry.getKey(), 90, 60);
            } else {
                color = "hsl("+colorizeService.generateColorHue(fieldName, entry.getKey())+",90%,50%)";
            }

            results.setWidget(row, 0, createLabel(entry.getKey(), color));

            results.setText(row, 1, entry.getValue().toString());

            results.setWidget(row, 2, new FieldActionsDropdown(fieldName, entry.getKey(), 0));
            row++;


            //plot
            final Series series = Series.of("", color);
            SeriesHandler handler = model.addSeries(series);
            final PieDataPoint datapoint = PieDataPoint.of(entry.getValue());
            handler.add(datapoint);
        }

        results.setWidget(row, 0, createLabel("Other", "#aaa"));
        results.setText(row,1,String.valueOf(response.getOther()));
        results.setText(row,2,"");
        row++;
        SeriesHandler otherHandler = model.addSeries(Series.of("", "#aaa"));
        otherHandler.add(PieDataPoint.of(response.getOther()));

        results.setWidget(row, 0, createLabel("Missing", "#eee"));
        results.setText(row,1,String.valueOf(response.getMissing()));
        results.setText(row,2,"");
        row++;
        SeriesHandler missingHandler = model.addSeries(Series.of("", "#eee"));
        missingHandler.add(PieDataPoint.of(response.getMissing()));

        results.setText(row,0,"Total");
        results.setText(row, 1, String.valueOf(response.getTotal()));
        results.setText(row, 2, "");

        loadingPanel.setVisible(false);
        errorPanel.setVisible(false);
        nothingFoundPanel.setVisible(false);
        resultsPanel.setVisible(true);

        plot.redraw();
    }

    private FlowPanel createLabel(final String string, final String color)
    {
        FlowPanel fp = new FlowPanel();
        Icon icon = new Icon(IconType.SIGN_BLANK);
        DOM.setStyleAttribute(icon.getElement(), "color", color);
        DOM.setStyleAttribute(icon.getElement(),"marginRight", "5px");
        fp.add(icon);
        fp.add(new InlineLabel(string));
        return fp;
    }

    @Override
    public void loading()
    {
        loadingPanel.setVisible(true);
        errorPanel.setVisible(false);
        resultsPanel.setVisible(false);
        nothingFoundPanel.setVisible(false);
    }

    @Override
    public void error()
    {
        loadingPanel.setVisible(false);
        errorPanel.setVisible(true);
        resultsPanel.setVisible(false);
        nothingFoundPanel.setVisible(false);
    }

    @Override
    public void nothingFound()
    {
        loadingPanel.setVisible(false);
        errorPanel.setVisible(false);
        resultsPanel.setVisible(false);
        nothingFoundPanel.setVisible(true);
    }

    @UiHandler("tryAgain")
    public void onTryAgainClick(final ClickEvent event)
    {
        presenter.tryAgain();
    }

    @UiHandler("backToLogs")
    public void onBackToLogsClick(ClickEvent event) {
        placeController.goTo(new StreamPlace(application.getCurrentSearch().clone()));
    }
}