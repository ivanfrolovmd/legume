package md.frolov.legume.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.query.SearchQuery;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HeaderComponent extends Composite
{
    private WidgetInjector injector = WidgetInjector.INSTANCE;

    interface HeaderUiBinder extends UiBinder<Widget, HeaderComponent>
    {
    }

    private static HeaderUiBinder binder = GWT.create(HeaderUiBinder.class);

    @UiField
    TextBox searchQuery;
    @UiField
    DateBox fromDate;
    @UiField
    DateBox toDate;

    public HeaderComponent()
    {
        initWidget(binder.createAndBindUi(this));
    }

    @UiHandler("submitButton")
    public void onSubmitButtonClick(ClickEvent event) {
        SearchQuery query = new SearchQuery();
        query.setQuery(searchQuery.getText());
        query.setFromDate(fromDate.getValue());
        query.setToDate(toDate.getValue());
        injector.placeController().goTo(new StreamPlace(query));
    }

    @UiHandler("resetButton")
    public void onResetButtonClick(ClickEvent event) {
        searchQuery.setText("");
    }
}