package md.frolov.legume.client.ui.components;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.MappingsRequest;
import md.frolov.legume.client.elastic.api.MappingsResponse;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.service.ColorizeService;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class FieldsComponent extends Composite
{
    interface FieldsComponentUiBinder extends UiBinder<Widget, FieldsComponent>
    {
    }
    interface Css extends CssResource {
        String verticalLine();
    }

    private static final Logger LOG = Logger.getLogger("FieldsComponent");
    private static FieldsComponentUiBinder binder = GWT.create(FieldsComponentUiBinder.class);
    private ElasticSearchService elasticSearchService = WidgetInjector.INSTANCE.elasticSearchService();
    private ColorizeService colorizeService = WidgetInjector.INSTANCE.colorizeService();

    @UiField
    Accordion typesAccordion;

    @UiField
    Css css;
    @UiField
    FlowPanel loadingPanel;
    @UiField
    FlowPanel errorPanel;
    @UiField
    Button tryAgain;

    public FieldsComponent()
    {
        initWidget(binder.createAndBindUi(this));

        initFields();
    }

    private void initFields() {
        loadingPanel.setVisible(true);
        typesAccordion.setVisible(false);
        errorPanel.setVisible(false);

        typesAccordion.clear();

        elasticSearchService.query(new MappingsRequest(), new Callback<MappingsRequest, MappingsResponse>()
        {
            @Override
            public void onFailure(final Throwable exception)
            {
                LOG.log(Level.SEVERE, "Exception",exception);
                loadingPanel.setVisible(false);
                typesAccordion.setVisible(false);
                errorPanel.setVisible(true);
            }

            @Override
            public void onSuccess(final MappingsRequest query, final MappingsResponse response)
            {
                StringBuilder sb = new StringBuilder();

                addGlobalProperties();
                for (Map.Entry<String, Set<MappingsResponse.Property>> typeEntry : response.getProperties().entrySet())
                {
                    addType(typeEntry.getKey(), typeEntry.getValue());
                }

                loadingPanel.setVisible(false);
                typesAccordion.setVisible(true);
                errorPanel.setVisible(false);
            }
        });
    }

    private void addType(String type, Set<MappingsResponse.Property> properties) {
        AccordionGroup accordionGroup = new AccordionGroup();
        accordionGroup.setHeading(type);
        Icon icon = new Icon();
        icon.addStyleName(css.verticalLine());
        DOM.setStyleAttribute(icon.getElement(), "color", colorizeService.getCssColor("@type", type, 100, 40));
        accordionGroup.addCustomTrigger(icon);

        for (MappingsResponse.Property property : properties)
        {
            accordionGroup.add(new FieldDropdown(property.getFullName(), property.getName(), property.getType()));
        }

        typesAccordion.add(accordionGroup);
    }

    private void addGlobalProperties() {
        AccordionGroup accordionGroup = new AccordionGroup();
        accordionGroup.setHeading("Common properties");
        Icon icon = new Icon();
        icon.addStyleName(css.verticalLine());
        DOM.setStyleAttribute(icon.getElement(), "color", "#999");
        accordionGroup.addCustomTrigger(icon);

        accordionGroup.add(new FieldDropdown("@type","@type","string"));
        accordionGroup.add(new FieldDropdown("@tags","@tags","string"));
        accordionGroup.add(new FieldDropdown("@source","@source","string"));
        accordionGroup.add(new FieldDropdown("@source_host","@source_host","string"));
        accordionGroup.add(new FieldDropdown("@source_path","@source_path","string"));
        accordionGroup.add(new FieldDropdown("@timestamp","@timestamp","date"));
        accordionGroup.add(new FieldDropdown("@message","@message","string"));

        typesAccordion.add(accordionGroup);
    }

    @UiHandler("tryAgain")
    public void onTryAgainClick(final ClickEvent event)
    {
        initFields();
    }
}