package md.frolov.legume.client.ui.controls;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.DropdownContainer;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ColorPicker extends Composite implements HasChangeHandlers
{
    interface ColorPickerUiBinder extends UiBinder<Widget, ColorPicker>
    {
    }

    interface CSS extends CssResource {
        String cellButton();
    }

    private static ColorPickerUiBinder binder = GWT.create(ColorPickerUiBinder.class);
    @UiField
    DropdownContainer dropdown;
    @UiField
    FlexTable colorTable;

    @UiField
    CSS css;

    private boolean addedWidgets = false;

    private List<ChangeHandler> changeHandlers = Lists.newArrayList();

    private int hue;

    public ColorPicker()
    {
        initWidget(binder.createAndBindUi(this));
        initColorTable();
    }

    private void initColorTable() {
        int hue = 0;
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                Button color = new Button();
                color.setIconSize(IconSize.DEFAULT);
                color.setStyleName(null);
                DOM.setStyleAttribute(color.getElement(), "backgroundColor", "hsl("+hue+",80%,98%)");
                DOM.setStyleAttribute(color.getElement(), "border", "2px solid hsl("+hue+",100%,40%)");
                color.addStyleName(css.cellButton());

                final int theHue = hue;
                color.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(final ClickEvent event)
                    {
                        setHue(theHue);
                        for (ChangeHandler handler : changeHandlers)
                        {
                            handler.onChange(null);
                        }
                        dropdown.hideContainer();
                    }
                });

                colorTable.setWidget(i,j,color);
                hue+=14;
            }
        }
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = (Math.abs(hue) % 360);

        int hueIterator = 0;
        boolean isSet = false;
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                Button color = (Button) colorTable.getWidget(i,j);

                if(hueIterator>=hue && !isSet) {
                    isSet = true;
                    color.setIcon(IconType.OK);
                } else {
                    color.setIcon(null);
                }

                hueIterator += 14;
            }
        }

        DOM.setStyleAttribute(dropdown.getElement(), "borderLeftColor", "hsl("+hue+",100%,40%)");
        DOM.setStyleAttribute(dropdown.getElement(), "backgroundColor", "hsl("+hue+",80%,98%)");
    }

    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        changeHandlers.add(handler);
        return new HandlerRegistration()
        {
            @Override
            public void removeHandler()
            {
                changeHandlers.remove(handler);
            }
        };
    }

    public void setText(String text) {
        dropdown.setText(text);
    }

    public void addWidget(Widget widget) {
        if(!addedWidgets) {
            dropdown.add(new Divider());
            addedWidgets = true;
        }
        dropdown.add(widget);
    }
}