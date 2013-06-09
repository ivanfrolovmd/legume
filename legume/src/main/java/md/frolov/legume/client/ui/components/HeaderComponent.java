package md.frolov.legume.client.ui.components;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.response.PingResponse;
import md.frolov.legume.client.elastic.query.Ping;
import md.frolov.legume.client.elastic.query.Search;
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
    Button openInNewWindow;
    @UiField
    Icon statusIconChecking;
    @UiField
    Icon statusIconOk;
    @UiField
    Icon statusIconError;

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();
    private ElasticSearchService elasticSearchService = WidgetInjector.INSTANCE.elasticSearchService();

    public HeaderComponent()
    {
        initWidget(binder.createAndBindUi(this));

        initStatusIcon();

        eventBus.addHandler(UpdateSearchQuery.TYPE, this);
    }

    private void initStatusIcon() {
        final AsyncCallback<PingResponse> callback = new AsyncCallback<PingResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                statusIconError.setVisible(true);
                statusIconChecking.setVisible(false);
                statusIconOk.setVisible(false);
            }

            @Override
            public void onSuccess(final PingResponse result)
            {
                if(result.getStatus() == 200) {
                    statusIconOk.setVisible(true);
                    statusIconError.setVisible(false);
                    statusIconChecking.setVisible(false);
                } else {
                    onFailure(null);
                }
            }
        };

        elasticSearchService.query(new Ping(), callback, PingResponse.class);
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                statusIconChecking.setVisible(true);
                statusIconError.setVisible(false);
                statusIconOk.setVisible(false);
                elasticSearchService.query(new Ping(), callback, PingResponse.class);
                return true;
            }
        }, 30000);
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
        Search query = new Search();
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
        Search query = event.getSearchQuery();
        searchQuery.setText(query.getQuery());
        fromDate.setValue(query.getFromDate());
        toDate.setValue(query.getToDate());

        String token = new StreamPlace.Tokenizer().getToken(new StreamPlace(query));
        openInNewWindow.setTargetHistoryToken(StreamPlace.TOKEN_PREFIX + ":" + token);
    }


}