package md.frolov.legume.client.ui.modals;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.service.ColorizeService;
import md.frolov.legume.client.ui.controls.ColorPicker;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ColorizeDialog
{
    interface ColorizeDialogUiBinder extends UiBinder<Modal, ColorizeDialog>
    {
    }

    private static ColorizeDialogUiBinder binder = GWT.create(ColorizeDialogUiBinder.class);

    private final String fieldName;

    @UiField
    Modal modal;
    @UiField
    Button okButton;
    @UiField
    Button cancelButton;
    @UiField
    TextBox termText;
    @UiField
    Button addTerm;
    @UiField
    Button colorizeToggle;
    @UiField
    Button highlightLabelsToggle;
    @UiField
    Button highlightBackgroundsToggle;
    @UiField
    FlowPanel editContainer;
    @UiField
    WellForm form;
    @UiField
    Label noTermsLabel;
    @UiField
    Button randomizeField;
    @UiField
    Button clearFields;

    Map<String, Integer> colorsMap = Maps.newHashMap();

    private boolean refreshRequired;

    private ColorizeService colorizeService = WidgetInjector.INSTANCE.colorizeService();

    public ColorizeDialog(String fieldName, @Nullable String value)
    {
        binder.createAndBindUi(this);

        this.fieldName = fieldName;
        modal.setTitle("Highlight <strong>"+fieldName+"</strong> with color");

        if(colorizeService.isFieldColorizable(fieldName)) {
            colorizeToggle.setActive(true);
            highlightLabelsToggle.setActive(colorizeService.getLabelFields().contains(fieldName));
            highlightBackgroundsToggle.setActive(colorizeService.getBackgroundFields().contains(fieldName));

            Set<String> values = colorizeService.getValues (fieldName);
            for (String val : values)
            {
                addValue(val, colorizeService.getColorHue(fieldName, val));
            }
        } else {
            refreshRequired = true;
            colorizeToggle.setActive(false);
            highlightLabelsToggle.setEnabled(false);
            highlightBackgroundsToggle.setEnabled(false);
            editContainer.setVisible(false);
        }
    }

    private void addValue(final String val, final int colorHue)
    {
        noTermsLabel.setVisible(false);

        colorsMap.put(val, colorHue);

        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setText(val);
        colorPicker.setHue(colorHue);

        colorPicker.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                int hue = colorPicker.getHue();
                colorsMap.put(val, hue);
            }
        });

        NavLink removeLink = new NavLink("Remove");
        removeLink.setIcon(IconType.REMOVE);
        colorPicker.addWidget(removeLink);
        removeLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                form.remove(colorPicker);
                colorsMap.remove(val);
            }
        });

        form.add(colorPicker);
    }

    public void show() {
        modal.show();
    }

    @UiHandler("colorizeToggle")
    public void onToggleColorize(final ClickEvent event)
    {
        if(colorizeToggle.isToggled()) {
            highlightLabelsToggle.setEnabled(false);
            highlightBackgroundsToggle.setEnabled(false);
            editContainer.setVisible(false);
        } else {
            highlightLabelsToggle.setEnabled(true);
            highlightBackgroundsToggle.setEnabled(true);
            editContainer.setVisible(true);
        }
    }

    @UiHandler("addTerm")
    public void onAddTermClick(final ClickEvent event)
    {
        addValue(termText.getText(), Random.nextInt(360));
        termText.setText("");
    }

    @UiHandler("randomizeField")
    public void onRandomizeClick(final ClickEvent event)
    {
        form.clear();
        Map<String, Integer> map = Maps.newHashMap(colorsMap);
        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            addValue(entry.getKey(), Random.nextInt(360));
        }
    }

    @UiHandler("clearFields")
    public void onClearFieldsClick(final ClickEvent event)
    {
        form.clear();
        colorsMap.clear();
    }

    @UiHandler("okButton")
    public void onOkClick(final ClickEvent event)
    {
        if(colorizeToggle.isToggled()) {
            //activating
            colorizeService.addColorizableField(fieldName);
            if(highlightLabelsToggle.isToggled()) {
                colorizeService.addLabelField(fieldName);
            } else {
                colorizeService.removeLabelField(fieldName);
            }
            if(highlightBackgroundsToggle.isToggled()) {
                colorizeService.addBackgroundField(fieldName);
            } else {
                colorizeService.removeBackgroundField(fieldName);
            }

            colorizeService.getValues(fieldName).clear();
            for (Map.Entry<String, Integer> entry : colorsMap.entrySet())
            {
                colorizeService.setColorHue(fieldName, entry.getKey(), entry.getValue());
            }
        } else {
            colorizeService.removeColorizableField(fieldName);
        }
        colorizeService.saveAndRefresh();
        modal.hide();
    }

    @UiHandler("cancelButton")
    public void onCancelClick(final ClickEvent event)
    {
        modal.hide();
    }
}