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

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.activities.terms.TermsPlace;
import md.frolov.legume.client.elastic.api.TermsFacetRequest;
import md.frolov.legume.client.elastic.query.Search;
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

    public FieldDropdown(String fullname, String name, String type)
    {
        fieldFullName = fullname;

        initWidget(binder.createAndBindUi(this));

        dropDown.setText(name);
    }

    @UiHandler("score")
    public void onScoreClick(final ClickEvent event)
    {
        //TODO refactor this. Implement global state handler that would store current search
        Search search = ((StreamPlace)placeController.getWhere()).getQuery();
        TermsFacetRequest request = TermsFacetRequest.create().withField(fieldFullName).withDatesFilter(search.getFromDate(), search.getToDate()).withQueryFilter(search.getQuery());
        TermsPlace place = new TermsPlace(request);
        placeController.goTo(place);
    }
}