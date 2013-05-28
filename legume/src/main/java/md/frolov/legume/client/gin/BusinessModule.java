package md.frolov.legume.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.impl.ElasticSearchServiceImpl;
import md.frolov.legume.client.service.ConfigurationService;
import md.frolov.legume.client.service.impl.ConfigurationServiceImpl;

public class BusinessModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ConfigurationService.class).to(ConfigurationServiceImpl.class);
        bind(ElasticSearchService.class).to(ElasticSearchServiceImpl.class);
    }

}
