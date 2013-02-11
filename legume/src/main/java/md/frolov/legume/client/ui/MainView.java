package md.frolov.legume.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite implements AcceptsOneWidget {

    private static MainViewUiBinder uiBinder = GWT.create(MainViewUiBinder.class);

    interface MainViewUiBinder extends UiBinder<Widget, MainView> {
    }
    
    @UiField
    Panel viewContainer;

    public MainView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setWidget(IsWidget w) {
        viewContainer.clear();
        viewContainer.add(w);
    }

}
