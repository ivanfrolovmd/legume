package md.frolov.legume.client.activities.stream;

import com.google.gwt.user.client.ui.IsWidget;

public interface StreamView extends IsWidget {

    void focusOnDate(long focusDate);

    interface Presenter {
        void requestMoreResults(boolean top);
    }

    void setPresenter(Presenter presenter);
}
