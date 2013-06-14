package md.frolov.legume.client.activities;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public abstract class SearchPlace extends Place
{
    private final Search search;

    public SearchPlace(final Search search)
    {
        this.search = search;
    }

    public Search getSearch()
    {
        return search;
    }

    public abstract PlaceTokenizer getTokenizer();
    public abstract String getTokenPrefix();

    public String getTargetHistoryToken() {
        return getTokenPrefix()+":"+getTokenizer().getToken(this);
    }
}
