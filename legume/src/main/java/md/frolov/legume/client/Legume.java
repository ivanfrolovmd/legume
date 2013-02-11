package md.frolov.legume.client;


import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.gin.WidgetInjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Legume implements EntryPoint {
  private WidgetInjector injector = WidgetInjector.INSTANCE;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
      injector.activityManager().setDisplay(injector.mainView());
      injector.placeHistoryHandler().register(injector.placeController(), injector.eventBus(), new StreamPlace()); //TODO change to homeplace

      RootPanel.get().add(injector.mainView());
      injector.placeHistoryHandler().handleCurrentHistory();
  }
}
