package md.frolov.legume.client;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.activities.terms.TermsPlace;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.events.UpdateSearchQueryHandler;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
@Singleton
public class Application implements PlaceChangeEvent.Handler, UpdateSearchQueryHandler
{
    @Inject
    private EventBus eventBus;

    @Inject
    private PlaceController placeController;

    private Search currentSearch;

    public Search getCurrentSearch()
    {
        return currentSearch;
    }

    @Override
    public void onPlaceChange(final PlaceChangeEvent event)
    {
        Place place = event.getNewPlace();
        if(place instanceof SearchPlace) {
            Search oldSearch = currentSearch;
            currentSearch = ((SearchPlace) place).getSearch().clone();

            if(oldSearch==null || !oldSearch.equals(currentSearch)) {
                eventBus.fireEvent(new UpdateSearchQuery(currentSearch));
            }
        }
    }

    @Override
    public void onUpdateSearchQuery(final UpdateSearchQuery event)
    {
        Place currentPlace = placeController.getWhere();
        if(currentPlace instanceof TermsPlace) {
            placeController.goTo(new TermsPlace(((TermsPlace) currentPlace).getFieldName(), event.getSearchQuery()));
            return;
        }
        placeController.goTo(new StreamPlace(event.getSearchQuery()));
    }
}
