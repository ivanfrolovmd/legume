package md.frolov.legume.client.gin;

import javax.inject.Singleton;

import md.frolov.legume.client.ui.MainView;

import com.google.gwt.inject.client.AbstractGinModule;

public class ViewModule extends AbstractGinModule{

    @Override
    protected void configure() {
        bind(MainView.class).in(Singleton.class);
    }

}
