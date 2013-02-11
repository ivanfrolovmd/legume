package md.frolov.legume.client.mvp;

import javax.inject.Inject;
import javax.inject.Provider;

import md.frolov.legume.client.activities.stream.StreamActivity;
import md.frolov.legume.client.activities.stream.StreamPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {
    @Inject
    private Provider<StreamActivity> albumsActivity;

    @Override
    public Activity getActivity(Place place) {
        // TODO make it configurable, mappable. Get rid of ifs.
        if(place instanceof StreamPlace) 
            return albumsActivity.get();
        return null;
    }

}
