package md.frolov.legume.client.ui.components;

import java.util.Comparator;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.events.ColorConfUpdatedEvent;
import md.frolov.legume.client.events.ColorConfUpdatedEventHandler;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.service.ColorizeService;
import md.frolov.legume.client.ui.modals.ColorizeDialog;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ColorFieldsComponent extends Composite implements ColorConfUpdatedEventHandler
{
    interface ColorFieldsComponentUiBinder extends UiBinder<Widget, ColorFieldsComponent>
    {
    }

    private static ColorFieldsComponentUiBinder binder = GWT.create(ColorFieldsComponentUiBinder.class);
    @UiField
    FlowPanel fieldContainer;

    private ColorizeService colorizeService = WidgetInjector.INSTANCE.colorizeService();

    public ColorFieldsComponent()
    {
        initWidget(binder.createAndBindUi(this));
        WidgetInjector.INSTANCE.eventBus().addHandler(ColorConfUpdatedEvent.TYPE, this);

        update();
    }

    @Override
    public void onColorConfUpdated(final ColorConfUpdatedEvent event)
    {
        update();
    }

    private void update()
    {
        Set<String> fields = Sets.newTreeSet(new FieldComparator());
        fields.addAll(colorizeService.getColorizableFields());
        fieldContainer.clear();
        FlowPanel allPanel = new FlowPanel();
        for (final String field : fields)
        {
            FlowPanel fp = new FlowPanel();

            Button label = new Button(field);
            label.setType(ButtonType.LINK);
            label.setSize(ButtonSize.SMALL);
            label.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(final ClickEvent event)
                {
                    new ColorizeDialog(field, null).show();
                }
            });

            final Button labelButton = new Button("L");
            labelButton.setType(ButtonType.LINK);
            labelButton.setToggle(true);
            labelButton.setSize(ButtonSize.MINI);
            labelButton.setActive(colorizeService.getLabelFields().contains(field));
            labelButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(final ClickEvent event)
                {
                    if(labelButton.isToggled()) {
                        //untoggling
                        colorizeService.removeLabelField(field);
                    } else {
                        colorizeService.addLabelField(field);
                    }
                    colorizeService.saveAndRefresh();
                }
            });

            final Button bgButton = new Button("B");
            bgButton.setType(ButtonType.LINK);
            bgButton.setToggle(true);
            bgButton.setSize(ButtonSize.MINI);
            bgButton.setActive(colorizeService.getBackgroundFields().contains(field));
            bgButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(final ClickEvent event)
                {
                    if(bgButton.isToggled()) {
                        //untoggling
                        colorizeService.removeBackgroundField(field);
                    } else {
                        colorizeService.addBackgroundField(field);
                    }
                    colorizeService.saveAndRefresh();
                }
            });

            Button removeButton = new Button("", IconType.REMOVE);
            removeButton.setType(ButtonType.LINK);
            removeButton.setSize(ButtonSize.MINI);
            removeButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(final ClickEvent event)
                {
                    colorizeService.removeColorizableField(field);
                    colorizeService.saveAndRefresh();
                }
            });

            fp.add(labelButton);
            fp.add(bgButton);
            fp.add(label);
            fp.add(removeButton);

            allPanel.add(fp);
        }
        fieldContainer.add(allPanel);
    }

    private class FieldComparator implements Comparator<String> {
        private static final String FIELDS_PREFIX = "@fields.";

        @Override
        public int compare(final String s1, final String s2)
        {
            if(s1.startsWith(FIELDS_PREFIX) && !s2.startsWith(FIELDS_PREFIX)) {
                return 1;
            }
            if(!s1.startsWith(FIELDS_PREFIX) && s2.startsWith(FIELDS_PREFIX)) {
                return -1;
            }

            return s1.compareTo(s2);
        }
    }
}
