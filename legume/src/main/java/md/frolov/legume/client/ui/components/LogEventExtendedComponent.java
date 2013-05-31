package md.frolov.legume.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventExtendedComponent
{
    interface LogEventExtendedComponentUiBinder extends UiBinder<FlowPanel, LogEventExtendedComponent>
    {
    }

    private static LogEventExtendedComponentUiBinder ourUiBinder = GWT.create(LogEventExtendedComponentUiBinder.class);

    public LogEventExtendedComponent()
    {
        FlowPanel rootElement = ourUiBinder.createAndBindUi(this);

    }
}