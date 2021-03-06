package md.frolov.legume.client.events;

import com.google.gwt.event.shared.EventHandler;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ScrollableStateChangedEventHandler extends EventHandler
{

    void onScrollableStateChanged(ScrollableStateChangedEvent event);
}
