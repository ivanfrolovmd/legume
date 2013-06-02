package md.frolov.legume.client.ui.components;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.query.SearchQuery;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.events.UpdateSearchQueryHandler;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HeaderComponent extends Composite implements UpdateSearchQueryHandler
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
    @UiField
    ListBox commonTimes;
    @UiField
    Anchor openInNewWindow;

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();

    public HeaderComponent()
    {
        initWidget(binder.createAndBindUi(this));

        eventBus.addHandler(UpdateSearchQuery.TYPE, this);
    }

    @UiHandler("submitButton")
    public void onSubmitButtonClick(ClickEvent event) {
        submitSearch();
    }

    @UiHandler("searchQuery")
    public void handleKeyPress(final KeyPressEvent event)
    {
        if(event.getCharCode()==13 || event.getCharCode() == 10){
            submitSearch();
        }
    }

    private void submitSearch()
    {
        SearchQuery query = new SearchQuery();
        query.setQuery(searchQuery.getText());
        query.setFromDate(fromDate.getValue());
        query.setToDate(toDate.getValue());
        injector.placeController().goTo(new StreamPlace(query));
    }

    @UiHandler("commonTimes")
    public void onPredefinedTimeChange(final ChangeEvent event)
    {
        Long time = Long.valueOf(commonTimes.getValue(commonTimes.getSelectedIndex()));
        if(time ==-1) {
            return;
        }
        if(time == 0) {
            fromDate.setValue(null);
        } else {
            fromDate.setValue(new Date(new Date().getTime()-time));
        }
        toDate.setValue(null);
        submitSearch();
    }

    @UiHandler("resetButton")
    public void onResetButtonClick(ClickEvent event) {
        searchQuery.setText("");
        fromDate.setValue(null);
        toDate.setValue(null);
    }

    @Override
    public void onUpdateSearchQuery(final UpdateSearchQuery event)
    {
        SearchQuery query = event.getSearchQuery();
        searchQuery.setText(query.getQuery());
        fromDate.setValue(query.getFromDate());
        toDate.setValue(query.getToDate());

        String token = new StreamPlace.Tokenizer().getToken(new StreamPlace(query));
        openInNewWindow.setHref("#"+ StreamPlace.TOKEN_PREFIX+":"+token);
    }


}