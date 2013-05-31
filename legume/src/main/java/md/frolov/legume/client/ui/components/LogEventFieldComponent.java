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

    public LogEventFieldComponent(String key, Object value)
    {
        initWidget(binder.createAndBindUi(this));
        this.key.setText(key);
        this.value.setText(value.toString()); //TODO format different types
    }

    @UiHandler("includeButton")
    public void onIncludeClick(final ClickEvent event)
    {
    }

    @UiHandler("excludeButton")
    public void onExcludeClick(final ClickEvent event)
    {
    }

}