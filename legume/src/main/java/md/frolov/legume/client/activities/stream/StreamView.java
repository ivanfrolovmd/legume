package md.frolov.legume.client.activities.stream;

import com.google.gwt.user.client.ui.IsWidget;

import md.frolov.legume.client.elastic.model.SearchHits;

public interface StreamView extends IsWidget {
    
    interface Presenter {
        
    }

    void handleLogMessages(SearchHits hits);
}
