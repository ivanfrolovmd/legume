package md.frolov.legume.client.gin;

import md.frolov.legume.client.service.ConfigurationService;
import md.frolov.legume.client.service.impl.ConfigurationServiceImpl;

import com.google.gwt.inject.client.AbstractGinModule;

public class BusinessModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ConfigurationService.class).to(ConfigurationServiceImpl.class);
    }

}
