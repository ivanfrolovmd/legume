package md.frolov.legume.client.mvp;

import javax.inject.Inject;
import javax.inject.Provider;

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
    @Inject
    private Provider<StreamActivity> streamActivity;

    @Inject
    private Provider<TermsActivity> termsActivity;

    private SearchActivity lastActivity;

    @Override
    public Activity getActivity(Place place) {
        if(lastActivity != null && place instanceof SearchPlace && lastActivity.canActivityBeReused((SearchPlace) place)) {
            return lastActivity;
        }

        // TODO make it configurable, mappable. Get rid of ifs.
        SearchActivity activity = null;
        if(place instanceof StreamPlace)
            activity = streamActivity.get();
        if(place instanceof TermsPlace)
            activity = termsActivity.get();

        lastActivity = activity;

        return activity;
    }

}
