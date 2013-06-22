package md.frolov.legume.client.ui;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.events.ScrollableStateChangedEvent;
import md.frolov.legume.client.events.ScrollableStateChangedEventHandler;

public class MainView extends Composite implements AcceptsOneWidget, ScrollableStateChangedEventHandler {

    private static MainViewUiBinder uiBinder = GWT.create(MainViewUiBinder.class);

    interface MainViewUiBinder extends UiBinder<Widget, MainView> {
    }

    interface Css extends CssResource{
        String hasScrolls();
    }
    
    @UiField
    Panel viewContainer;
    @UiField
    Css css;
    @UiField
    FlowPanel histogram;

    @Inject
    public MainView(EventBus eventBus) {
        initWidget(uiBinder.createAndBindUi(this));

        eventBus.addHandler(ScrollableStateChangedEvent.TYPE, this);
    }

    @Override
    public void setWidget(IsWidget w) {
        viewContainer.clear();
        if(w!=null) {
            viewContainer.add(w);
        }
    }

    @Override
    public void onScrollableStateChanged(final ScrollableStateChangedEvent event)
    {
        if(event.isScrollable()){
            histogram.addStyleName(css.hasScrolls());
        } else {
            histogram.removeStyleName(css.hasScrolls());
        }
    }
}
