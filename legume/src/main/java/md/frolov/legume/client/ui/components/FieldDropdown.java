package md.frolov.legume.client.ui.components;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.Application;
import md.frolov.legume.client.activities.terms.TermsPlace;
import md.frolov.legume.client.gin.WidgetInjector;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class FieldDropdown extends Composite
{
    interface FieldDropdownUiBinder extends UiBinder<Widget, FieldDropdown>
    {
    }

    private static FieldDropdownUiBinder binder = GWT.create(FieldDropdownUiBinder.class);
    @UiField
    Dropdown dropDown;
    @UiField
    NavLink score;
    @UiField
    NavLink colorize;

    private String fieldFullName;
    private PlaceController placeController = WidgetInjector.INSTANCE.placeController();
    private Application application = WidgetInjector.INSTANCE.application();

    public FieldDropdown(String fullname, String name, String type)
    {
        fieldFullName = fullname;

        initWidget(binder.createAndBindUi(this));

        dropDown.setText(name);
    }

    @UiHandler("score")
    public void onScoreClick(final ClickEvent event)
    {
        score.setTargetHistoryToken(new TermsPlace(fieldFullName, application.getCurrentSearch()).getTargetHistoryToken());
    }
}