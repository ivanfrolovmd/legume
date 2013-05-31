package md.frolov.legume.client.events;

import com.google.gwt.event.shared.EventHandler;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface LogMessageEventHandler extends EventHandler
{
    void onLogMessage(LogMessageEvent event);
}
