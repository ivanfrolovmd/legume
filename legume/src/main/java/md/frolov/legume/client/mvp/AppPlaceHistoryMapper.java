package md.frolov.legume.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import md.frolov.legume.client.activities.config.ConfigPlace;
import md.frolov.legume.client.activities.stream.StreamPlace;

@WithTokenizers({ StreamPlace.Tokenizer.class, ConfigPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
