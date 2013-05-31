package md.frolov.legume.client.ui.components;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.elastic.model.LogEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventExtendedComponent extends Composite
{
    interface LogEventExtendedComponentUiBinder extends UiBinder<Widget, LogEventExtendedComponent>
    {
    }

    private static LogEventExtendedComponentUiBinder binder = GWT.create(LogEventExtendedComponentUiBinder.class);

    @UiField
    VerticalPanel container;

    public LogEventExtendedComponent(LogEvent logEvent)
    {
        initWidget(binder.createAndBindUi(this));

        fillIn(logEvent);
    }

    private void fillIn(final LogEvent logEvent)
    {
        container.add(new LogEventFieldComponent("time",logEvent.getTimestamp())); //TODO localize?
        container.add(new LogEventFieldComponent("type", logEvent.getType()));
        container.add(new LogEventFieldComponent("message", logEvent.getMessage()));
        container.add(new LogEventFieldComponent("source", logEvent.getSource()));
        container.add(new LogEventFieldComponent("source host", logEvent.getSourceHost()));

        String tags = Joiner.on(", ").join(logEvent.getTags());
        container.add(new LogEventFieldComponent("tags", tags));

        Map<String, List<String>> fields = ImmutableSortedMap.copyOf(logEvent.getFields());
        for (Iterator<Map.Entry<String, List<String>>> iterator = fields.entrySet().iterator(); iterator.hasNext(); )
        {
            Map.Entry<String, List<String>> entry = iterator.next();

            String value = Joiner.on(", ").join(entry.getValue());
            container.add(new LogEventFieldComponent(entry.getKey(), value));
        }
    }
}