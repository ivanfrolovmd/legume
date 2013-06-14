package md.frolov.legume.client.activities.terms;

import com.google.gwt.user.client.ui.IsWidget;

import md.frolov.legume.client.elastic.api.TermsFacetResponse;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface TermsView extends IsWidget
{
    void handleResults(final String fieldName, TermsFacetResponse response);
}
