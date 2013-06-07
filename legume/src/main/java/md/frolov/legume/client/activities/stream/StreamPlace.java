package md.frolov.legume.client.activities.stream;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import md.frolov.legume.client.elastic.query.Search;

public class StreamPlace extends Place {
    public final static String TOKEN_PREFIX = "stream";
    private final Search query;

    public StreamPlace(final Search query)
    {
        this.query = query;
    }

    @Prefix(TOKEN_PREFIX)
    public static class Tokenizer implements PlaceTokenizer<StreamPlace> {
        @Override
        public StreamPlace getPlace(String token) {
            Search query = Search.fromHistoryToken(token);
            return new StreamPlace(query);
        }

        @Override
        public String getToken(StreamPlace place) {
            return place.getQuery().toHistoryToken();
        }

    }

    public Search getQuery()
    {
        return query;
    }
}
