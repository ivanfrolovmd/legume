package md.frolov.legume.client.gin;

import java.util.List;
import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.storage.client.Storage;
import com.google.inject.Provides;

import md.frolov.legume.client.Application;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.ElasticSearchServiceImpl;
import md.frolov.legume.client.service.ColorizeService;
import md.frolov.legume.client.service.ConfigurationService;
import md.frolov.legume.client.service.StorageDao;
import md.frolov.legume.client.service.impl.ColorizeServiceImpl;
import md.frolov.legume.client.service.impl.ConfigurationServiceImpl;
import md.frolov.legume.client.service.impl.CookieStorageDao;
import md.frolov.legume.client.service.impl.Html5StorageDao;
import md.frolov.legume.client.service.impl.JsPropertiesStorageDao;

public class BusinessModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(Application.class).in(Singleton.class);

        bind(ConfigurationService.class).to(ConfigurationServiceImpl.class);
        bind(ElasticSearchService.class).to(ElasticSearchServiceImpl.class);
        bind(ColorizeService.class).to(ColorizeServiceImpl.class);
    }

    @Provides
    @Singleton
    public List<StorageDao> getStorageDaos() {
        List<StorageDao> storageDaos = Lists.newArrayList();
        if(Storage.isLocalStorageSupported()) {
            storageDaos.add(new Html5StorageDao());
        }
        storageDaos.add(new CookieStorageDao());
        storageDaos.add(new JsPropertiesStorageDao());

        return storageDaos;
    }
}
