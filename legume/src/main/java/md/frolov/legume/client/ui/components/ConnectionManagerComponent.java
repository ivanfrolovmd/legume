package md.frolov.legume.client.ui.components;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconPosition;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.Constants;
import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.configuration.ConnectionsConf;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.PingRequest;
import md.frolov.legume.client.elastic.api.PingResponse;
import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.query.Search;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.service.ConfigurationService;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConnectionManagerComponent extends Composite implements Constants
{
    interface ConnectionManagerComponentUiBinder extends UiBinder<Widget, ConnectionManagerComponent>
    {
    }

    private static ConnectionManagerComponentUiBinder binder = GWT.create(ConnectionManagerComponentUiBinder.class);

    @UiField
    Dropdown dropDown;
    @UiField
    NavLink editConfs;
    @UiField
    NavLink addNew;
    @UiField
    Modal addModal;
    @UiField
    Button addButton;
    @UiField
    TextBox serviceName;
    @UiField
    TextBox serviceAddress;

    private ElasticSearchService elasticSearchService = WidgetInjector.INSTANCE.elasticSearchService();
    private ConfigurationService configurationService = WidgetInjector.INSTANCE.configurationService();
    private ModelFactory modelFactory = WidgetInjector.INSTANCE.modelFactory();
    private PlaceController placeController = WidgetInjector.INSTANCE.placeController();

    public ConnectionManagerComponent()
    {
        initWidget(binder.createAndBindUi(this));

        initStatusIcon();
    }

    private void initStatusIcon()
    {
        final Callback<PingRequest, PingResponse> callback = new Callback<PingRequest, PingResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                dropDown.setIcon(IconType.BAN_CIRCLE);
            }

            @Override
            public void onSuccess(final PingRequest request, final PingResponse response)
            {
                if (response.getPingReply().getStatus() == 200)
                {
                    dropDown.setIcon(IconType.CIRCLE);
                }
                else
                {
                    onFailure(null);
                }
            }
        };

        elasticSearchService.query(new PingRequest(), callback);
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                dropDown.setIcon(IconType.CIRCLE_BLANK);
                elasticSearchService.query(new PingRequest(), callback);
                return true;
            }
        }, 30000);
    }

    @UiHandler("dropDown")
    public void onDropdownClick(final ClickEvent event)
    {
        dropDown.clear();
        ConnectionsConf connectionsConf = configurationService.getObject(ELASTICSEARCH_CONNECTIONS, ConnectionsConf.class);
        String currentConnection = configurationService.get(ELASTICSEARCH_SERVER);
        if (connectionsConf != null && connectionsConf.getConnections() != null && !connectionsConf.getConnections().isEmpty())
        {
            for (final Map.Entry<String, String> connection : connectionsConf.getConnections().entrySet())
            {
                NavLink link = new NavLink();
                link.setText(connection.getKey());
                if (connection.getValue().equals(currentConnection))
                {
                    link.setIcon(IconType.OK);
                    link.setIconPosition(IconPosition.RIGHT);
                }
                dropDown.add(link);

                link.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(final ClickEvent event)
                    {
                        selectServer(connection.getValue());
                    }
                });
            }
            dropDown.add(new Divider());
        }
        dropDown.add(addNew);
        dropDown.add(editConfs);
    }

    private void selectServer(String serverAddress) {
        configurationService.put(ELASTICSEARCH_SERVER, serverAddress);
        placeController.goTo(new StreamPlace(Search.DEFAULT));
    }

    @UiHandler("addNew")
    public void onShowAddModal(final ClickEvent event)
    {
        serviceName.setText("");
        serviceAddress.setText("");
        addModal.show();
    }

    @UiHandler("addButton")
    public void onAddButtonClick(final ClickEvent event)
    {
        ConnectionsConf conf = configurationService.getObject(ELASTICSEARCH_CONNECTIONS, ConnectionsConf.class);
        if (conf == null)
        {
            conf = modelFactory.connectionsConf().as();
        }
        Map<String, String> connections = conf.getConnections();
        if (connections == null)
        {
            connections = Maps.newTreeMap();
        }
        connections.put(serviceName.getText(), serviceAddress.getText());
        conf.setConnections(connections);
        configurationService.put(ELASTICSEARCH_CONNECTIONS, conf);
        addModal.hide();

        selectServer(serviceAddress.getText());
    }
}