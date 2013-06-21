package md.frolov.legume.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public abstract class SearchActivity extends AbstractActivity
{
    public abstract boolean canActivityBeReused(SearchPlace newPlace);
}
