package md.frolov.legume.client.gin;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.impl.ElasticSearchServiceImpl;
import md.frolov.legume.client.elastic.model.ModelFactory;
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

    @Provides
    public ModelFactory getAutoBeanModelFactory() {
        return ModelFactory.INSTANCE;
    }
}
