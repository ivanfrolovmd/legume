package md.frolov.legume.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.model.Search;

/** Entry point classes define <code>onModuleLoad()</code>. */
public class Legume implements EntryPoint
{
    private static final Logger LOG = Logger.getLogger("Legume");

    private WidgetInjector injector = WidgetInjector.INSTANCE;

    private ResourceBundle resources = GWT.create(ResourceBundle.class);

    /** This is the entry point method. */
    public void onModuleLoad()
    {
        resources.mainCss().ensureInjected();

        injector.eventBus().addHandler(PlaceChangeEvent.TYPE, injector.application());
        injector.eventBus().addHandler(UpdateSearchQuery.TYPE, injector.application());

        injector.activityManager().setDisplay(injector.mainView());
        RootLayoutPanel.get().add(injector.mainView());

        injector.placeHistoryHandler().register(injector.placeController(), injector.eventBus(), new StreamPlace(Search.DEFAULT));
        injector.placeHistoryHandler().handleCurrentHistory();

        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler()
        {
            @Override
            public void onUncaughtException(final Throwable e)
            {
                LOG.log(Level.SEVERE, "uncaught", e);
            }
        });
    }


}
