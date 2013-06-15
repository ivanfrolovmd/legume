package md.frolov.legume.client.ui.modals;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextArea;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class CopyToClipboardDialog
{
    interface CopyToClipboardDialogUiBinder extends UiBinder<Modal, CopyToClipboardDialog>
    {
    }

    private static CopyToClipboardDialogUiBinder binder = GWT.create(CopyToClipboardDialogUiBinder.class);

    @UiField
    Button close;
    @UiField
    TextArea textArea;
    @UiField
    Modal modal;

    public CopyToClipboardDialog(String fieldName, String text)
    {
        binder.createAndBindUi(this);
        modal.setTitle("Copy <strong>"+fieldName+"</strong>");
        textArea.setText(text);

        modal.addShownHandler(new ShownHandler()
        {
            @Override
            public void onShown(final ShownEvent shownEvent)
            {
                textArea.selectAll();
                textArea.setFocus(true);
            }
        });
    }

    public void show() {
        modal.show();
    }

    @UiHandler("close")
    public void onCloseClick(final ClickEvent event)
    {
        modal.hide();
    }
}