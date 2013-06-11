package md.frolov.legume.client.gin;

import javax.inject.Singleton;

import com.google.gwt.inject.client.AbstractGinModule;

import md.frolov.legume.client.activities.config.ConfigView;
import md.frolov.legume.client.activities.config.ConfigViewImpl;
import md.frolov.legume.client.activities.stream.StreamView;
import md.frolov.legume.client.activities.stream.StreamViewImpl;
import md.frolov.legume.client.ui.MainView;
import md.frolov.legume.client.ui.components.ConnectionManagerComponent;

public class ViewModule extends AbstractGinModule{

    @Override
    protected void configure() {
        bind(MainView.class).in(Singleton.class);

        bind(StreamView.class).to(StreamViewImpl.class);
        bind(ConfigView.class).to(ConfigViewImpl.class);
        bind(ConnectionManagerComponent.class);
    }

}
