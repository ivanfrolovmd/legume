package md.frolov.legume.client.mvp;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import md.frolov.legume.client.activities.config.ConfigActivity;
import md.frolov.legume.client.activities.config.ConfigPlace;
import md.frolov.legume.client.activities.stream.StreamActivity;
import md.frolov.legume.client.activities.stream.StreamPlace;

public class AppActivityMapper implements ActivityMapper {
    @Inject
    private Provider<StreamActivity> streamActivity;

    @Inject
    private Provider<ConfigActivity> configActivity;

    @Override
    public Activity getActivity(Place place) {
        // TODO make it configurable, mappable. Get rid of ifs.
        if(place instanceof StreamPlace)
            return streamActivity.get();
        if(place instanceof ConfigPlace)
            return configActivity.get();

        return null;
    }

}
