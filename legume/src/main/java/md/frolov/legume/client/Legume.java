package md.frolov.legume.client;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.query.Search;
import md.frolov.legume.client.events.LogMessageEvent;
import md.frolov.legume.client.gin.WidgetInjector;

/** Entry point classes define <code>onModuleLoad()</code>. */
public class Legume implements EntryPoint
{
    private static final Logger LOG = Logger.getLogger("Legume");

    private WidgetInjector injector = WidgetInjector.INSTANCE;

    /** This is the entry point method. */
    public void onModuleLoad()
    {
        injector.activityManager().setDisplay(injector.mainView());
        RootLayoutPanel.get().add(injector.mainView());

        Search query = new Search("", null, new Date(), new Date());
        injector.placeHistoryHandler().register(injector.placeController(), injector.eventBus(), new StreamPlace(query)); //TODO change to homeplace
        injector.placeHistoryHandler().handleCurrentHistory();

        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler()
        {
            @Override
            public void onUncaughtException(final Throwable e)
            {
                LOG.log(Level.SEVERE,"uncaught",e);
                injector.eventBus().fireEvent(new LogMessageEvent("Uncaught error: "+e.getMessage()));
            }
        });
    }

}
