package md.frolov.legume.client.ui.modals;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConfigurationModal
{

    interface ConfigurationModalUiBinder extends UiBinder<Modal, ConfigurationModal>
    {
    }

    private static ConfigurationModalUiBinder binder = GWT.create(ConfigurationModalUiBinder.class);
    @UiField
    Modal modal;
    @UiField
    Button okButton;
    @UiField
    Button cancelButton;

    public ConfigurationModal()
    {
        binder.createAndBindUi(this);
    }

    public void show()
    {
        modal.show();
    }

    @UiHandler("okButton")
    public void onOkButtonClick(final ClickEvent event)
    {
        modal.hide();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick(final ClickEvent event)
    {
        modal.hide();
    }
}