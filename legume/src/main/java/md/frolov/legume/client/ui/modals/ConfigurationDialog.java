package md.frolov.legume.client.ui.modals;

import java.util.Map;

import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DoubleBox;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Well;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import md.frolov.legume.client.Constants;
import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.model.ConnectionsConf;
import md.frolov.legume.client.service.ConfigurationService;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConfigurationDialog implements Constants
{

    interface ConfigurationModalUiBinder extends UiBinder<Modal, ConfigurationDialog>
    {
    }

    private static ConfigurationModalUiBinder binder = GWT.create(ConfigurationModalUiBinder.class);
    @UiField
    Modal modal;
    @UiField
    Button okButton;
    @UiField
    Well connectionsWell;
    @UiField
    Button addConnection;
    @UiField
    TextBox serverUrl;
    @UiField
    TextBox serverName;
    @UiField
    Button exportButton;
    @UiField
    Button importButton;
    @UiField
    FileUploadExt uploadFile;
    @UiField
    IntegerBox pageSize;
    @UiField
    IntegerBox timeout;
    @UiField
    DoubleBox scrollThreshold;

    private ConfigurationService configurationService = WidgetInjector.INSTANCE.configurationService();

    public ConfigurationDialog()
    {
        binder.createAndBindUi(this);

        initGeneralTab();
        initConnectionsTab();
    }

    private void initGeneralTab() {
        pageSize.setText(configurationService.get(PAGE_SIZE));
        timeout.setText(configurationService.get(ELASTICSEARCH_TIMEOUT));
        scrollThreshold.setText(configurationService.get(SCROLL_THREASHOLD_CLIENT_HEIGHT_RATIO));
    }

    private void initConnectionsTab(){
        ConnectionsConf connections = configurationService.getObject(ELASTICSEARCH_CONNECTIONS, ConnectionsConf.class);
        connectionsWell.clear();
        if(connections == null) {
            return;
        }
        for (Map.Entry<String, String> entry : connections.getConnections().entrySet())
        {
            addConnectionToWell(entry.getKey(), entry.getValue());
        }
    }

    private void addConnectionToWell(final String label, final String serverAddress) {
        final FlowPanel fp = new FlowPanel();
        InlineLabel labelLabel = new InlineLabel(label);
        InlineLabel serverLabel = new InlineLabel(serverAddress);
        Button removeButton = new Button("", IconType.REMOVE);
        removeButton.setSize(ButtonSize.MINI);
        fp.add(labelLabel);
        fp.add(serverLabel);
        fp.add(removeButton);

        removeButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                fp.removeFromParent();
                ConnectionsConf connections = configurationService.getObject(ELASTICSEARCH_CONNECTIONS,ConnectionsConf.class);
                connections.getConnections().remove(label);
                configurationService.put(ELASTICSEARCH_CONNECTIONS, connections);
            }
        });

        connectionsWell.add(fp);
    }

    @UiHandler("addConnection")
    public void onAddConnectionClick(final ClickEvent event)
    {
        ConnectionsConf connections = configurationService.getObject(ELASTICSEARCH_CONNECTIONS, ConnectionsConf.class);
        ConnectionsConf newConnections = ModelFactory.INSTANCE.connectionsConf().as();
        Map<String, String> connectionsMap = Maps.newHashMap();
        if(connections!=null) {
            connectionsMap.putAll(connections.getConnections());
        }
        String name = serverName.getText();
        String url = serverUrl.getText();
        connectionsMap.put(name, url);
        newConnections.setConnections(connectionsMap);
        configurationService.put(ELASTICSEARCH_CONNECTIONS, newConnections);

        initConnectionsTab();

        serverName.setText("");
        serverUrl.setText("");
    }

    @UiHandler("exportButton")
    public void onExportButtonClick(final ClickEvent event)
    {
        exportButton.setHref(configurationService.exportConfig());
        DOM.setElementAttribute(exportButton.getElement(), "download", "settings.json");
    }

    @UiHandler("importButton")
    public void onImportButtonClick(final ClickEvent event)
    {
        uploadFile.click();
    }

    @UiHandler("uploadFile")
    public void onUploadFile(final ChangeEvent event)
    {
        FileList files = uploadFile.getFiles();
        if(files.getLength()>0) {
            File file = files.getItem(0);
            final FileReader reader = new FileReader();

            reader.addLoadEndHandler(new LoadEndHandler()
            {
                @Override
                public void onLoadEnd(final LoadEndEvent event)
                {
                    String text = reader.getStringResult();
                    configurationService.importConfig(text);
                    initConnectionsTab();
                    initGeneralTab();
                    //TODO show success
                }
            });

            reader.readAsText(file);
        }
    }

    @UiHandler("pageSize")
    public void onPageSizeChange(final BlurEvent event)
    {
        int pSize = pageSize.getValue();
        if(pSize>0) {
            configurationService.put(PAGE_SIZE, String.valueOf(pSize));
        }
    }

    @UiHandler("timeout")
    public void onTimeoutChange(final BlurEvent event)
    {
        int tOut = timeout.getValue();
        if(tOut>0) {
            configurationService.put(ELASTICSEARCH_TIMEOUT, String.valueOf(tOut));
        }
    }

    @UiHandler("scrollThreshold")
    public void handleBlur(final BlurEvent event)
    {
        double threshold = scrollThreshold.getValue();
        if(threshold>0) {
            configurationService.put(SCROLL_THREASHOLD_CLIENT_HEIGHT_RATIO, String.valueOf(threshold));
        }
    }

    public void show()
    {
        modal.show();
    }

    @UiHandler("okButton")
    public void onOkButtonClick(final ClickEvent event)
    {
        modal.hide();
        Window.Location.reload();
    }
}