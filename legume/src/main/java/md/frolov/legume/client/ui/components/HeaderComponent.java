package md.frolov.legume.client.ui.components;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.Application;
import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.events.UpdateSearchQueryHandler;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.model.Search;

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
    Button openInNewWindow;

    private EventBus eventBus = WidgetInjector.INSTANCE.eventBus();
    private Application application = WidgetInjector.INSTANCE.application();

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
        Search query = application.getCurrentSearch().clone();
        query.setQuery(searchQuery.getText());;
        injector.placeController().goTo(new StreamPlace(query)); //TODO change this. 'Terms' activity would like to stay there
    }

    @Override
    public void onUpdateSearchQuery(final UpdateSearchQuery event)
    {
        Search query = event.getSearchQuery();
        searchQuery.setText(query.getQuery());
    }

    @UiHandler("openInNewWindow")
    public void onOpenInNewWindowClick(final ClickEvent event)
    {
        String place = new StreamPlace(application.getCurrentSearch()).getTargetHistoryToken();
        openInNewWindow.setTargetHistoryToken(place);
    }
}