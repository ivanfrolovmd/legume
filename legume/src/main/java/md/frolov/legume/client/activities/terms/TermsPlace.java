package md.frolov.legume.client.activities.terms;

import java.util.Iterator;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.History;

import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsPlace extends SearchPlace
{
    private static final String TOKEN_PREFIX = "terms";

    private final String fieldName;

    public TermsPlace(final String fieldName, final Search search)
    {
        super(search);
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    @Prefix(TOKEN_PREFIX)
    public static class Tokenizer implements PlaceTokenizer<TermsPlace>
    {
        @Override
        public TermsPlace getPlace(String token)
        {
            try {
                Iterable<String> parts = Splitter.on('/').limit(5).split(token);
                Iterator<String> it = parts.iterator();

                Long fromDateTime = Long.valueOf(it.next());
                Long toDateTime = Long.valueOf(it.next());
                Long focusDateTime = Long.valueOf(it.next());

                String field = it.next();
                String query = it.next();

                return new TermsPlace(field, new Search(query, fromDateTime, toDateTime, focusDateTime));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String getToken(TermsPlace place)
        {
            Search search = place.getSearch();
            return Joiner.on("/").join(new Object[]{
                    search.getFromDate(),
                    search.getToDate(),
                    search.getFocusDate(),
                    History.encodeHistoryToken(place.getFieldName()),
                    History.encodeHistoryToken(search.getQuery())
            });
        }
    }
}
