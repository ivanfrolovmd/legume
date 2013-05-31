package md.frolov.legume.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import md.frolov.legume.client.elastic.model.LogEvent;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.util.ColorUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventComponent extends Composite
{
    private static final DateTimeFormat DTF = DateTimeFormat.getFormat("hh:MM:ss.sss");

    interface LogEventComponentUiBinder extends UiBinder<FlowPanel, LogEventComponent>
    {
    }

    private static LogEventComponentUiBinder binder = GWT.create(LogEventComponentUiBinder.class);

    private final String id;
    private final LogEvent logEvent;

    private WidgetInjector injector = WidgetInjector.INSTANCE;
    private ColorUtils colorUtils = injector.colorUtils();

    @UiField
    Button more;
    @UiField
    Label time;
    @UiField
    Label type;
    @UiField
    Label source;
    @UiField
    Label message;

    public LogEventComponent(String id, LogEvent logEvent)
    {
        initWidget(binder.createAndBindUi(this));
        this.id = id;
        this.logEvent = logEvent;

        time.setText(DTF.format(logEvent.getTimestamp()));
        message.setText(logEvent.getMessage());
        DOM.setStyleAttribute(message.getElement(), "whiteSpace", "pre");

        String typeColor = colorUtils.getColor(logEvent.getType());
        DOM.setStyleAttribute(type.getElement(), "backgroundColor", typeColor);
        type.setText(logEvent.getType());

        String sourceColor = colorUtils.getColor(logEvent.getSourceHost());
        DOM.setStyleAttribute(source.getElement(), "backgroundColor", sourceColor);
        source.setText(logEvent.getSourceHost());
    }

    @UiHandler("more")
    public void handleClick(final ClickEvent event)
    {

    }

}