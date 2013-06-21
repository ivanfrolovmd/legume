package md.frolov.legume.client.activities.terms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.activities.SearchActivity;
import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.TermsFacetRequest;
import md.frolov.legume.client.elastic.api.TermsFacetResponse;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsActivity extends SearchActivity implements TermsView.Presenter
{
    private static final Logger LOG = Logger.getLogger("TermsActivity");

    @Inject
    private TermsView termsView;

    @Inject
    private PlaceController placeController;

    @Inject
    private ElasticSearchService elasticSearchService;

    private TermsFacetRequest request;
    private String currentFieldName;
    private Search currentSearch;

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(termsView);
        termsView.setPresenter(this);

        TermsPlace place = (TermsPlace) placeController.getWhere();
        currentFieldName = place.getFieldName();
        currentSearch = place.getSearch();
        request = new TermsFacetRequest(place.getFieldName(), place.getSearch());
        query();
    }

    private void query() {
        termsView.loading();

        elasticSearchService.query(request, new Callback<TermsFacetRequest, TermsFacetResponse>()
        {
            @Override
            public void onFailure(final Throwable exception)
            {
                LOG.log(Level.SEVERE, "Can't fetch terms score", exception);
                termsView.error();
            }

            @Override
            public void onSuccess(final TermsFacetRequest query, final TermsFacetResponse response)
            {
                termsView.handleResults(query.getFieldName(), response);
            }
        });
    }

    @Override
    public void tryAgain()
    {
        query();
    }

    @Override
    public boolean canActivityBeReused(final SearchPlace newPlace)
    {
        if(!(newPlace instanceof TermsPlace)) {
            return false;
        }

        return currentFieldName.equals(((TermsPlace) newPlace).getFieldName())
                && Strings.nullToEmpty(currentSearch.getQuery()).equals(Strings.nullToEmpty(newPlace.getSearch().getQuery()))
                && currentSearch.getFromDate() == newPlace.getSearch().getFromDate()
                && currentSearch.getToDate() == newPlace.getSearch().getToDate();
    }
}
