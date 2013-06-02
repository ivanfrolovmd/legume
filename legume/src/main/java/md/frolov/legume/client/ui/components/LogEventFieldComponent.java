package md.frolov.legume.client.ui.components;

import java.util.Date;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.model.LogEvent;
import md.frolov.legume.client.elastic.query.SearchQuery;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.util.ConversionUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventFieldComponent extends Composite
{
    private static final RegExp SOLR_SPECIAL_CHARS = RegExp.compile("[-+&|!(){}\\[\\]^\"~*?:\\\\]","g");

    interface LogEventFieldComponentUiBinder extends UiBinder<Widget, LogEventFieldComponent>
    {
    }

    private static LogEventFieldComponentUiBinder binder = GWT.create(LogEventFieldComponentUiBinder.class);

    @UiField
    Label valueLabel;
    @UiField
    Label keyLabel;
    @UiField
    Hyperlink includeFilter;
    @UiField
    Hyperlink excludeFilter;

    private final Place place;
    private final String queryKey;

    public LogEventFieldComponent(String key, String queryKey, Object value, LogEvent logEvent)
    {
        initWidget(binder.createAndBindUi(this));
        keyLabel.setText(key);
        valueLabel.setText(getStringValue(value));
        this.queryKey = queryKey;
        place = WidgetInjector.INSTANCE.placeController().getWhere();

        String filter = getFilter();
        SearchQuery query = ((StreamPlace) place).getQuery().clone();
        String originalQueryString = query.getQuery();
        StreamPlace.Tokenizer tokenizer = new StreamPlace.Tokenizer();

        query.setFocusDate(logEvent.getTimestamp());
        query.setQuery(getQueryString(originalQueryString, filter));
        includeFilter.setTargetHistoryToken("stream:" + tokenizer.getToken(new StreamPlace(query)));
        query.setQuery(getQueryString(originalQueryString, "NOT " + filter));
        excludeFilter.setTargetHistoryToken("stream:"+tokenizer.getToken(new StreamPlace(query)));
    }

    private String getStringValue(Object value) {
        String ret = null;
        if(value instanceof Date) {
            ret = ConversionUtils.INSTANCE.dateToString((Date) value);
        } else
        if(value instanceof Iterable) {
            ret = Joiner.on(", ").skipNulls().join(((Iterable) value).iterator());
        } else if (value != null) {
            ret = value.toString();
        }

        if(Strings.isNullOrEmpty(ret)){
            ret = "\u00A0"; //nbsp
        }
        return ret;
    }

    private String getQueryString(String originalQuery, String postfix)
    {
        if (originalQuery.length() != 0)
        {
            return originalQuery + " AND " + postfix;
        }
        else
        {
            return postfix;
        }
    }

    private String getFilter()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(queryKey).append(":\"");
        sb.append(SOLR_SPECIAL_CHARS.replace(valueLabel.getText(),"\\$&"));
        sb.append('\"');

        return sb.toString();
    }
}