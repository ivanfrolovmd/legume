package md.frolov.legume.client.gin;

import javax.inject.Named;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.Window;
import com.google.inject.Provides;

import md.frolov.legume.client.Constants;
import md.frolov.legume.client.service.ConfigurationService;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConstantsModule extends AbstractGinModule implements Constants
{
    @Override
    protected void configure()
    {
    }

    @Provides
    @Named("scrollThreashold")
    public Integer configureScrollThreashold(ConfigurationService configurationService)
    {
        double ratio = configurationService.getDouble(SCROLL_THREASHOLD_CLIENT_HEIGHT_RATIO, 1);
        int threashold = (int) (Window.getClientHeight() * ratio);
        if(threashold < 50) {
            threashold = 50;
        }
        if(threashold> 10000) {
            threashold = 10000;
        }
        return threashold;
    }
}
