package md.frolov.legume.client.gin;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import md.frolov.legume.client.mvp.AppActivityMapper;
import md.frolov.legume.client.mvp.AppPlaceHistoryMapper;

public class MVPModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ActivityMapper.class).to(AppActivityMapper.class);
        bind(PlaceHistoryMapper.class).to(AppPlaceHistoryMapper.class);

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

    }

    @Provides
    @Singleton
    @Inject
    public ActivityManager getActivityManager(ActivityMapper mapper, EventBus eventBus) {
        return new ActivityManager(mapper, eventBus);
    }

    @Provides
    @Singleton
    @Inject
    public PlaceHistoryHandler getPlaceHistoryHandler(PlaceHistoryMapper mapper) {
        return new PlaceHistoryHandler(mapper);
    }

    @Provides
    @Singleton
    @Inject
    public PlaceController getPlaceController(EventBus eventBus) {
        return new PlaceController(eventBus);
    }

}
