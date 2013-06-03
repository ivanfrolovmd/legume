package md.frolov.legume.client.activities.config;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConfigPlace extends Place
{
    public static final String TOKEN_PREFIX = "config";

    @Prefix(TOKEN_PREFIX)
    public static class Tokenizer implements PlaceTokenizer<ConfigPlace>
    {
        @Override
        public ConfigPlace getPlace(final String token)
        {
                return new ConfigPlace();
        }

        @Override
        public String getToken(final ConfigPlace place)
        {
            return "";
        }
    }
}
