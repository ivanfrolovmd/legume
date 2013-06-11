package md.frolov.legume.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.ElasticSearchServiceImpl;
import md.frolov.legume.client.service.ConfigurationService;
import md.frolov.legume.client.service.impl.ConfigurationServiceImpl;
import md.frolov.legume.client.util.ColorUtils;

public class BusinessModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ConfigurationService.class).to(ConfigurationServiceImpl.class);
        bind(ElasticSearchService.class).to(ElasticSearchServiceImpl.class);

        bind(ColorUtils.class);
    }
}
