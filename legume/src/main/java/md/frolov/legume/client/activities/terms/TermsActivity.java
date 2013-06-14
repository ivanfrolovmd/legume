package md.frolov.legume.client.activities.terms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.TermsFacetRequest;
import md.frolov.legume.client.elastic.api.TermsFacetResponse;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsActivity extends AbstractActivity
{
    private static final Logger LOG = Logger.getLogger("TermsActivity");

    @Inject
    private TermsView termsView;

    @Inject
    private PlaceController placeController;

    @Inject
    private ElasticSearchService elasticSearchService;

    private TermsFacetRequest request;

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(termsView);

        TermsPlace place = (TermsPlace) placeController.getWhere();
        request = new TermsFacetRequest(place.getFieldName(), place.getSearch());
        query();
    }

    private void query() {
        //TODO show loading in UI

        elasticSearchService.query(request, new Callback<TermsFacetRequest, TermsFacetResponse>()
        {
            @Override
            public void onFailure(final Throwable exception)
            {
                LOG.log(Level.SEVERE, "Can't fetch terms score", exception);
                //TODO show in UI
            }

            @Override
            public void onSuccess(final TermsFacetRequest query, final TermsFacetResponse response)
            {
                termsView.handleResults(query.getFieldName(), response);
            }
        });
    }

}
