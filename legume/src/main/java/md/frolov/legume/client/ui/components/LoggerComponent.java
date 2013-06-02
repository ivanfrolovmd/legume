package md.frolov.legume.client.ui.components;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.events.LogMessageEvent;
import md.frolov.legume.client.events.LogMessageEventHandler;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LoggerComponent extends Composite implements LogMessageEventHandler
{
    private final EventBus eventBus = WidgetInjector.INSTANCE.eventBus();

    interface LoggerComponentUiBinder extends UiBinder<Widget, LoggerComponent>
    {
    }

    private static LoggerComponentUiBinder binder = GWT.create(LoggerComponentUiBinder.class);

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    Panel container;

    public LoggerComponent()
    {
        initWidget(binder.createAndBindUi(this));
        eventBus.addHandler(LogMessageEvent.TYPE, this);
    }

    @Override
    public void onLogMessage(final LogMessageEvent event)
    {
        container.add(new Label(new Date() + ". " +event.getMessage()));
        scrollPanel.scrollToBottom();
    }
}