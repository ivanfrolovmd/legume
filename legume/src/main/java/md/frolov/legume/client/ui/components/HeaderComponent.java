package md.frolov.legume.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

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

    public HeaderComponent()
    {
        initWidget(binder.createAndBindUi(this));
    }

    @UiHandler("submitButton")
    public void onSubmitButtonClick(ClickEvent event) {
        injector.placeController().goTo(new StreamPlace(new SearchQuery(searchQuery.getText())));
    }

    @UiHandler("resetButton")
    public void onResetButtonClick(ClickEvent event) {
        searchQuery.setText("");
    }
}