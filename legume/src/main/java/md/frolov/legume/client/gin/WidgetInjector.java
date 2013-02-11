package md.frolov.legume.client.gin;

import md.frolov.legume.client.Messages;
import md.frolov.legume.client.ui.MainView;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.web.bindery.event.shared.EventBus;

@GinModules({BusinessModule.class, MVPModule.class, ViewModule.class})
public interface WidgetInjector extends Ginjector {
    WidgetInjector INSTANCE = GWT.create(WidgetInjector.class);
    
    MainView mainView();
    
    Messages messages();
    
    EventBus eventBus();
    ActivityManager activityManager();
    PlaceHistoryHandler placeHistoryHandler();
    PlaceController placeController();
}
