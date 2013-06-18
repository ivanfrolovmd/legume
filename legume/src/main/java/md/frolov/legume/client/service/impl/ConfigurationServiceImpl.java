package md.frolov.legume.client.service.impl;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.model.GlobalConf;
import md.frolov.legume.client.service.ConfigurationService;
import md.frolov.legume.client.service.StorageDao;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService
{
    @Inject
    private ModelFactory modelFactory;

    @Inject
    private List<StorageDao> storageDaos;

    @Override
    public String get(final String key)
    {
        return get(key, "");
    }

    @Override
    public String get(final String key, final String defaultValue)
    {
        String value = null;
        for (StorageDao storageDao : storageDaos)
        {
            try {
                value = storageDao.get(key);
            } catch (Exception e){
                //do nothing
            }
            if(value != null) {
                break;
            }
        }
        if(value == null) {
            value = defaultValue;
        }
        return value;
    }

    @Override
    public int getInt(final String key)
    {
        return Integer.valueOf(get(key, "0"));
    }

    @Override
    public double getDouble(final String key, final double defaultValue)
    {
        return Double.valueOf(get(key, String.valueOf(defaultValue)));
    }

    @Override
    public void put(final String key, final String value)
    {
        for (StorageDao storageDao : storageDaos)
        {
            try{
                storageDao.put(key,value);
                return;
            } catch (Exception e) {
                //continue
            }
        }

        throw new IllegalStateException("Can't save property");
    }

    @Override
    public Map<String, String> getPropertyMap()
    {
        List<StorageDao> reversedList = Lists.reverse(storageDaos);
        Map<String, String> result = Maps.newHashMap();
        for (StorageDao dao : reversedList)
        {
            result.putAll(dao.getAllProperties());
        }

        return result;
    }

    @Override
    public <T> T getObject(final String key, final Class<T> clazz)
    {
        try{
            String json = get(key);
            return AutoBeanCodex.decode(modelFactory, clazz, json).as();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public <T> void put(final String key, final T object)
    {
        String json = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(object)).getPayload();
        put(key, json);
    }

    @Override
    public String exportConfig()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("data:application/text;charset=utf-8,");

        GlobalConf conf = ModelFactory.INSTANCE.globalConf().as();
        conf.setProperties(getPropertyMap());
        sb.append(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(conf)).getPayload());
        return sb.toString();
    }

    @Override
    public void importConfig(final String importJson)
    {
        GlobalConf conf = AutoBeanCodex.decode(ModelFactory.INSTANCE, GlobalConf.class, importJson).as();
        for (Map.Entry<String, String> prop : conf.getProperties().entrySet())
        {
            put(prop.getKey(), prop.getValue());
        }
    }
}
