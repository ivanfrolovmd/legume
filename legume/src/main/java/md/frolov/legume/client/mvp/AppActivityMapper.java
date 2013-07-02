package md.frolov.legume.client.mvp;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.collect.Maps;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import md.frolov.legume.client.activities.SearchActivity;
import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.activities.stream.StreamActivity;
import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.activities.terms.TermsActivity;
import md.frolov.legume.client.activities.terms.TermsPlace;

public class AppActivityMapper implements ActivityMapper {
    private final Map<Class<? extends Place>, Provider<? extends SearchActivity>> activityMap;

    private SearchActivity lastActivity;

    @Inject
    public AppActivityMapper(Provider<StreamActivity> streamActivity, Provider<TermsActivity> termsActivity)
    {
        activityMap = Maps.newHashMap();
        activityMap.put(StreamPlace.class, streamActivity);
        activityMap.put(TermsPlace.class, termsActivity);
    }

    @Override
    public Activity getActivity(Place place) {
        if(lastActivity != null && place instanceof SearchPlace && lastActivity.canActivityBeReused((SearchPlace) place)) {
            return lastActivity;
        }

        SearchActivity activity = activityMap.get(place.getClass()).get();
        lastActivity = activity;

        return activity;
    }

}
