package md.frolov.legume.client.ui.components;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.MappingsRequest;
import md.frolov.legume.client.elastic.api.MappingsResponse;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.util.ColorUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class FieldsComponent extends Composite
{
    interface FieldsComponentUiBinder extends UiBinder<Widget, FieldsComponent>
    {
    }

    private static final Logger LOG = Logger.getLogger("FieldsComponent");
    private static FieldsComponentUiBinder binder = GWT.create(FieldsComponentUiBinder.class);
    private ElasticSearchService elasticSearchService = WidgetInjector.INSTANCE.elasticSearchService();
    private ColorUtils colorUtils = WidgetInjector.INSTANCE.colorUtils();

    @UiField
    Accordion typesAccordion;

    public FieldsComponent()
    {
        initWidget(binder.createAndBindUi(this));

        initFields();
    }

    private void initFields() {
        typesAccordion.clear();

        elasticSearchService.query(new MappingsRequest(), new Callback<MappingsRequest, MappingsResponse>()
        {
            @Override
            public void onFailure(final Throwable exception)
            {
                LOG.log(Level.SEVERE, "Exception",exception);
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
            }
        });
    }

    private void addType(String type, Set<MappingsResponse.Property> properties) {
        AccordionGroup accordionGroup = new AccordionGroup();
        accordionGroup.setHeading(type);
        Icon icon = new Icon(IconType.CIRCLE);
        icon.setIconSize(IconSize.SMALL);
        DOM.setStyleAttribute(icon.getElement(), "color", colorUtils.getHashColor(type, 100, 40));
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
        accordionGroup.setIcon(IconType.CIRCLE);

        accordionGroup.add(new FieldDropdown("@type","@type","string"));
        accordionGroup.add(new FieldDropdown("@tags","@tags","string"));
        accordionGroup.add(new FieldDropdown("@source","@source","string"));
        accordionGroup.add(new FieldDropdown("@source_host","@source_host","string"));
        accordionGroup.add(new FieldDropdown("@source_path","@source_path","string"));
        accordionGroup.add(new FieldDropdown("@timestamp","@timestamp","date"));
        accordionGroup.add(new FieldDropdown("@message","@message","string"));

        typesAccordion.add(accordionGroup);
    }
}