package md.frolov.legume.client.activities.stream;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class StreamPlace extends Place {

    @Prefix("stream")
    public static class Tokenizer implements PlaceTokenizer<StreamPlace> {
        @Override
        public StreamPlace getPlace(String token) {
            return new StreamPlace();
        }

        @Override
        public String getToken(StreamPlace place) {
            return null;
        }

    }
}
