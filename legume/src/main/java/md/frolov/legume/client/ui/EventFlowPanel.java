package md.frolov.legume.client.ui;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class EventFlowPanel extends FlowPanel implements HasMouseOverHandlers, HasMouseOutHandlers, HasClickHandlers
{
    @Override
    public HandlerRegistration addClickHandler(final ClickHandler handler)
    {
        return addDomHandler(handler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseOutHandler(final MouseOutHandler handler)
    {
        return addDomHandler(handler, MouseOutEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler)
    {
        return addDomHandler(handler, MouseOverEvent.getType());
    }
}
