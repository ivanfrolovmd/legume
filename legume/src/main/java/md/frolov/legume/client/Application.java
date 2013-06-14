package md.frolov.legume.client;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.web.bindery.event.shared.EventBus;

import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
@Singleton
public class Application implements PlaceChangeEvent.Handler
{
    @Inject
    private EventBus eventBus;

    private Search currentSearch;

    public Search getCurrentSearch()
    {
        return currentSearch;
    }

    public void setCurrentSearch(final Search currentSearch)
    {
        this.currentSearch = currentSearch;
    }

    @Override
    public void onPlaceChange(final PlaceChangeEvent event)
    {
        Place place = event.getNewPlace();
        if(place instanceof SearchPlace) {
            Search oldSearch = currentSearch;
            currentSearch = ((SearchPlace) place).getSearch();

            if(oldSearch==null || !oldSearch.equals(currentSearch)) {
                eventBus.fireEvent(new UpdateSearchQuery(currentSearch));
            }
        }
    }
}
