package md.frolov.legume.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.events.AppendQueryFilter;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventFieldComponent extends Composite
{
    interface LogEventFieldComponentUiBinder extends UiBinder<Widget, LogEventFieldComponent>
    {
    }

    private static LogEventFieldComponentUiBinder binder = GWT.create(LogEventFieldComponentUiBinder.class);

    @UiField
    Button includeButton;
    @UiField
    Button excludeButton;
    @UiField
    Label value;
    @UiField
    Label key;

    private final EventBus eventBus = WidgetInjector.INSTANCE.eventBus();
    private final String queryKey;

    public LogEventFieldComponent(String key, String queryKey, Object value)
    {
        initWidget(binder.createAndBindUi(this));
        this.key.setText(key);
        this.value.setText(value.toString()); //TODO format different types
        this.queryKey = queryKey;
    }

    @UiHandler("includeButton")
    public void onIncludeClick(final ClickEvent event)
    {
        appendFilter(true);
    }


    @UiHandler("excludeButton")
    public void onExcludeClick(final ClickEvent event)
    {
        appendFilter(false);
    }

    private void appendFilter(final boolean positive)
    {
        StringBuilder sb = new StringBuilder();
        if(!positive) {
            sb.append("NOT ");
        }
        sb.append(queryKey).append(":\"");
        sb.append(value.getText());
        sb.append('\"');

        eventBus.fireEvent(new AppendQueryFilter(sb.toString()));
    }
}