package md.frolov.legume.client.activities.terms;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import md.frolov.legume.client.elastic.api.TermsFacetRequest;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsPlace extends Place
{
    public static final String TOKEN_PREFIX = "terms";

    private final TermsFacetRequest request;

    public TermsPlace(final TermsFacetRequest request)
    {
        this.request = request;
    }

    public TermsFacetRequest getRequest()
    {
        return request;
    }

    @Prefix(TOKEN_PREFIX)
        public static class Tokenizer implements PlaceTokenizer<TermsPlace>
        {
            @Override
            public TermsPlace getPlace(String token) {
                TermsFacetRequest request = TermsFacetRequest.fromHistoryToken(token);
                return new TermsPlace(request);
            }

            @Override
            public String getToken(TermsPlace place) {
                return place.getRequest().toHistoryToken();
            }

        }
}
