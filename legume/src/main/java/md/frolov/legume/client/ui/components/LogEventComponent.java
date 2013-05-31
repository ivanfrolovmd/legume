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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import md.frolov.legume.client.elastic.model.LogEvent;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.util.ColorUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventComponent extends Composite
{
    private static final DateTimeFormat DTF = DateTimeFormat.getFormat("dd/MM HH:mm:ss.sss");
    private static final int MAX_SUMMARY_WIDTH = 300;

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
    @UiField
    HorizontalPanel summary;
    @UiField
    Button less;
    @UiField
    FlowPanel details;
    @UiField
    FlowPanel detailsContainer;

    public LogEventComponent(String id, LogEvent logEvent)
    {
        initWidget(binder.createAndBindUi(this));
        this.id = id;
        this.logEvent = logEvent;

        time.setText(DTF.format(logEvent.getTimestamp()));
        String summaryText = abbreviate(logEvent.getMessage(), MAX_SUMMARY_WIDTH);
        message.setText(summaryText);

        String typeColor = colorUtils.getColor(logEvent.getType());
        DOM.setStyleAttribute(type.getElement(), "backgroundColor", typeColor);
        type.setText(logEvent.getType());

        String sourceColor = colorUtils.getColor(logEvent.getSourceHost());
        DOM.setStyleAttribute(source.getElement(), "backgroundColor", sourceColor);
        source.setText(logEvent.getSourceHost());
    }

    private String abbreviate(String string, int maxWidth)
    {
        if (string.length() < maxWidth)
        {
            return string;
        }
        else
        {
            return string.substring(0, maxWidth) + "...";
        }
    }

    @UiHandler("more")
    public void handleClickMore(final ClickEvent event)
    {
        detailsContainer.add(new LogEventExtendedComponent(logEvent));
        summary.setVisible(false);
        details.setVisible(true);
    }

    @UiHandler("less")
    public void handleClickLess(final ClickEvent event)
    {
        summary.setVisible(true);
        details.setVisible(false);
        detailsContainer.clear();
    }
}