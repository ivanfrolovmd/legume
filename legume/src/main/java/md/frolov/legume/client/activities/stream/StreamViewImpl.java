package md.frolov.legume.client.activities.stream;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.elastic.model.SearchHit;
import md.frolov.legume.client.elastic.model.SearchHits;
import md.frolov.legume.client.events.LogMessageEvent;
import md.frolov.legume.client.ui.components.LogEventComponent;

public class StreamViewImpl extends Composite implements StreamView {

    private static StreamViewImplUiBinder uiBinder = GWT.create(StreamViewImplUiBinder.class);

    @Inject
    private EventBus eventBus;

    @UiField
    FlowPanel container;

    interface StreamViewImplUiBinder extends UiBinder<Widget, StreamViewImpl> {
    }

    public StreamViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void handleLogMessages(final SearchHits hits)
    {
        container.setVisible(false);
        container.clear();

        long started = System.currentTimeMillis();
        for (SearchHit hit : hits.getHits())
        {
            LogEventComponent logEventComponent = new LogEventComponent(hit.getId(), hit.getLogEvent());
            container.add(logEventComponent);
        }

        container.setVisible(true);
        eventBus.fireEvent(new LogMessageEvent("Rendering took: "+(System.currentTimeMillis()-started)/1000.0));
    }
}
