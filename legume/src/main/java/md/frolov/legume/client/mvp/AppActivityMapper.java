package md.frolov.legume.client.mvp;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import md.frolov.legume.client.activities.stream.StreamActivity;
import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.activities.terms.TermsActivity;
import md.frolov.legume.client.activities.terms.TermsPlace;

public class AppActivityMapper implements ActivityMapper {
    @Inject
    private Provider<StreamActivity> streamActivity;

    @Inject
    private Provider<TermsActivity> termsActivity;

    @Override
    public Activity getActivity(Place place) {
        // TODO make it configurable, mappable. Get rid of ifs.
        if(place instanceof StreamPlace)
            return streamActivity.get();
        if(place instanceof TermsPlace)
            return termsActivity.get();

        return null;
    }

}
