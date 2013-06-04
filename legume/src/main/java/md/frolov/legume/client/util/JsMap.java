package md.frolov.legume.client.util;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class JsMap extends JavaScriptObject
{
    protected JsMap()
    { }


    public final String get(final String o) {
        return doGet(o).toString();
    }

    private final native Object doGet(final String o) /*-{
        return this[o].toString();
    }-*/;

    public final native String put(final String k, final String v) /*-{
        this[k]=v;
    }-*/;

    public final Set<String> keySet()
    {
        Set<String> result = Sets.newHashSet();
        JsArrayString keys = getAllKeys();
        for (int i = 0; i < keys.length(); i++)
        {
            String key = keys.get(i);
            result.add(key);
        }

        return result;
    }

    private final native JsArrayString getAllKeys() /*-{
        var keys = new Array();
        for(var key in this){
            keys.push(key);
        }
        return keys;
    }-*/;
}
