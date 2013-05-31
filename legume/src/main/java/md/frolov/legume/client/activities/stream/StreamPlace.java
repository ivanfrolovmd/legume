package md.frolov.legume.client.activities.stream;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import md.frolov.legume.client.elastic.query.SearchQuery;

public class StreamPlace extends Place {
    private final SearchQuery query;

    public StreamPlace(final SearchQuery query)
    {
        this.query = query;
    }

    @Prefix("stream")
    public static class Tokenizer implements PlaceTokenizer<StreamPlace> {
        @Override
        public StreamPlace getPlace(String token) {
            SearchQuery query = new SearchQuery();
            query.setQuery(token);
            return new StreamPlace(query);
        }

        @Override
        public String getToken(StreamPlace place) {
            return place.getQuery().getQuery();
        }

    }

    public SearchQuery getQuery()
    {
        return query;
    }
}
