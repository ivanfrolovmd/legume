package md.frolov.legume.client.activities.config;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.service.ConfigurationService;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConfigViewImpl extends Composite implements ConfigView
{
    interface ConfigViewImplUiBinder extends UiBinder<Widget, ConfigViewImpl>
    {
    }

    private static ConfigViewImplUiBinder binder = GWT.create(ConfigViewImplUiBinder.class);

    @UiField
    FlowPanel container;
    @UiField
    Form form;

    private ConfigurationService configurationService = WidgetInjector.INSTANCE.configurationService();

    public ConfigViewImpl()
    {
        initWidget(binder.createAndBindUi(this));

        initTable();
    }

    private void initTable()
    {
        Map<String, String> properties = configurationService.getPropertyMap();

        for (Map.Entry<String, String> entry : properties.entrySet())
        {
            addControl(entry.getKey(), entry.getValue());
        }
    }

    private void addControl(final String key, final String value)
    {
        ControlGroup controlGroup = new ControlGroup();
        ControlLabel controlLabel = new ControlLabel(key);
        Controls controls = new Controls();
        final TextBox textBox = new TextBox();
        textBox.setValue(value);
        controls.add(textBox);
        controlGroup.add(controlLabel);
        controlGroup.add(controls);
        form.add(controlGroup);

        textBox.addBlurHandler(new BlurHandler()
        {
            @Override
            public void onBlur(final BlurEvent event)
            {
                configurationService.put(key, textBox.getValue());
            }
        });
        textBox.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(final KeyPressEvent event)
            {
                if(event.getCharCode()==13 || event.getCharCode()==10) {
                    configurationService.put(key, textBox.getValue());
                }
            }
        });
    }

}