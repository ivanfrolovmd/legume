package md.frolov.legume.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ResourceBundle extends ClientBundle
{
    @ClientBundle.Source("fonts/OpenSans-Regular-webfont.woff")
    DataResource openSansRegularWoff();
    @ClientBundle.Source("fonts/OpenSans-Regular-webfont.ttf")
    DataResource openSansRegularTtf();
    @ClientBundle.Source("fonts/OpenSans-Regular-webfont.svg")
    DataResource openSansRegularSvg();
    @ClientBundle.Source("fonts/OpenSans-Regular-webfont.eot")
    DataResource openSansRegularEot();

    @ClientBundle.Source("fonts/OpenSans-Bold-webfont.woff")
    DataResource openSansBoldWoff();
    @ClientBundle.Source("fonts/OpenSans-Bold-webfont.ttf")
    DataResource openSansBoldTtf();
    @ClientBundle.Source("fonts/OpenSans-Bold-webfont.svg")
    DataResource openSansBoldSvg();
    @ClientBundle.Source("fonts/OpenSans-Bold-webfont.eot")
    DataResource openSansBoldEot();

    @ClientBundle.Source("fonts/OpenSans-Italic-webfont.woff")
    DataResource openSansItalicWoff();
    @ClientBundle.Source("fonts/OpenSans-Italic-webfont.ttf")
    DataResource openSansItalicTtf();
    @ClientBundle.Source("fonts/OpenSans-Italic-webfont.svg")
    DataResource openSansItalicSvg();
    @ClientBundle.Source("fonts/OpenSans-Italic-webfont.eot")
    DataResource openSansItalicEot();

    @ClientBundle.Source("fonts/OpenSans-Light-webfont.woff")
    DataResource openSansLightWoff();
    @ClientBundle.Source("fonts/OpenSans-Light-webfont.ttf")
    DataResource openSansLightTtf();
    @ClientBundle.Source("fonts/OpenSans-Light-webfont.svg")
    DataResource openSansLightSvg();
    @ClientBundle.Source("fonts/OpenSans-Light-webfont.eot")
    DataResource openSansLightEot();

    @ClientBundle.Source("Legume.css")
    CssResource mainCss();
}
