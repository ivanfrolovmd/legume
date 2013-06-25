package md.frolov.legume.client.activities.stream;

import java.util.Iterator;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.model.Search;

public class StreamPlace extends SearchPlace
{
    private final static String TOKEN_PREFIX = "stream";
    private final static Tokenizer TOKENIZER = new Tokenizer();

    public StreamPlace(final Search search)
    {
        super(search);
    }

    @Prefix(TOKEN_PREFIX)
    public static class Tokenizer implements PlaceTokenizer<StreamPlace> {
        @Override
        public StreamPlace getPlace(String token) {
            try{
                Iterator<String> parts = Splitter.on("/").limit(4).split(token).iterator();

                Long fromDate = Long.valueOf(parts.next());
                Long toDate= Long.valueOf(parts.next());
                Long focusDate = Long.valueOf(parts.next());
                String query = parts.next();

                return new StreamPlace(new Search(query, fromDate, toDate, focusDate));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String getToken(StreamPlace place) {
            Search search = place.getSearch();
            return Joiner.on("/").join(new Object[]{
                    search.getFromDate(),
                    search.getToDate(),
                    search.getFocusDate(),
                    search.getQuery()
            });
        }

    }
}

