package md.frolov.legume.client.activities;

import com.google.gwt.place.shared.Place;

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
}
