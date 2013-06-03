package md.frolov.legume.client.activities.config;

import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConfigActivity extends AbstractActivity
{
    @Inject
    private ConfigView configView;

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(configView);
    }
}
