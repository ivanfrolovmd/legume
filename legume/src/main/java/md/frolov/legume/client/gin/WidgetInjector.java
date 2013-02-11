package md.frolov.legume.client.gin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({BusinessModule.class, MVPModule.class})
public interface WidgetInjector extends Ginjector {
    WidgetInjector INSTANCE = GWT.create(WidgetInjector.class);
}
