package md.frolov.legume.client.activities.stream;

import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class StreamActivity extends AbstractActivity {
    @Inject
    private StreamView streamView;

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(streamView);
    }

}
