package md.frolov.legume.client.ui.components;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class FieldDropdown extends Composite
{
    interface FieldDropdownUiBinder extends UiBinder<Widget, FieldDropdown>
    {
    }

    private static FieldDropdownUiBinder binder = GWT.create(FieldDropdownUiBinder.class);
    @UiField
    Dropdown dropDown;

    public FieldDropdown(String fullname, String name, String type)
    {
        initWidget(binder.createAndBindUi(this));

        dropDown.setText(name);
    }
}