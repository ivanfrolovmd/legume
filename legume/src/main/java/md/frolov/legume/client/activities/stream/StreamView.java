package md.frolov.legume.client.activities.stream;

import com.google.gwt.user.client.ui.IsWidget;

import md.frolov.legume.client.elastic.api.SearchRequest;
import md.frolov.legume.client.elastic.api.SearchResponse;

public interface StreamView extends IsWidget {

    void focusOnDate(long focusDate);

    void showLoading();
    void showError();
    void showNothingFound();
    void showResults();

    void showLoading(boolean upwards);
    void handleNewHits(boolean upwards, SearchRequest searchRequest, SearchResponse searchResponse);
    void showError(boolean upwards);

    interface Presenter {
        void requestMoreResults(boolean top);

        void checkInitialRequests(boolean upwards);

        void tryAgain();
    }

    void setPresenter(Presenter presenter);
}
