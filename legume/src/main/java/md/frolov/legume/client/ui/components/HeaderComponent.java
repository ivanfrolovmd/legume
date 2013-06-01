package md.frolov.legume.client.ui.components;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.query.SearchQuery;
import md.frolov.legume.client.events.AppendQueryFilter;
import md.frolov.legume.client.events.AppendQueryFilterHandler;
import md.frolov.legume.client.events.SearchResultsReceivedEvent;
import md.frolov.legume.client.events.SearchResultsReceivedEventHandler;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.events.UpdateSearchQueryHandler;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HeaderComponent extends Composite implements UpdateSearchQueryHandler, AppendQueryFilterHandler, SearchResultsReceivedEventHandler
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
    Label hitsLabel;

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();

    public HeaderComponent()
    {
        initWidget(binder.createAndBindUi(this));

        eventBus.addHandler(UpdateSearchQuery.TYPE, this);
        eventBus.addHandler(AppendQueryFilter.TYPE, this);
        eventBus.addHandler(SearchResultsReceivedEvent.TYPE, this);
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
    public void handleChange(final ChangeEvent event)
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
    }

    @Override
    public void onAppendQueryFilter(final AppendQueryFilter event)
    {
        StringBuilder sb = new StringBuilder(searchQuery.getText());
        if(sb.length()>0) {
            sb.append(" AND ");
        }
        sb.append(event.getFilter());
        searchQuery.setText(sb.toString());
        submitSearch();
    }

    @Override
    public void onSearchResultsReceived(final SearchResultsReceivedEvent event)
    {
        long totalHits = event.getSearchResponse().getHits().getTotal();
        hitsLabel.setText("Hits: "+totalHits);
    }
}