package md.frolov.legume.client.ui.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
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

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventFieldComponent extends Composite
{
    interface LogEventFieldComponentUiBinder extends UiBinder<Widget, LogEventFieldComponent>
    {
    }

    private static LogEventFieldComponentUiBinder binder = GWT.create(LogEventFieldComponentUiBinder.class);

    @UiField
    Label value;
    @UiField
    Label key;
    @UiField
    Hyperlink includeFilter;
    @UiField
    Hyperlink excludeFilter;

    private final Place place;
    private final String queryKey;

    public LogEventFieldComponent(String key, String queryKey, Object value, LogEvent logEvent)
    {
        initWidget(binder.createAndBindUi(this));
        this.key.setText(key);
        this.value.setText(value.toString()); //TODO format different types
        this.queryKey = queryKey;
        place = WidgetInjector.INSTANCE.placeController().getWhere();

        String filter = getFilter();
        SearchQuery query = ((StreamPlace) place).getQuery().clone();
        String originalQueryString = query.getQuery();
        StreamPlace.Tokenizer tokenizer = new StreamPlace.Tokenizer();

        query.setFocusDate(logEvent.getTimestamp());
        query.setQuery(getQueryString(originalQueryString, filter));
        includeFilter.setTargetHistoryToken("stream:"+tokenizer.getToken(new StreamPlace(query)));
        query.setQuery(getQueryString(originalQueryString, "NOT " + filter));
        excludeFilter.setTargetHistoryToken("stream:"+tokenizer.getToken(new StreamPlace(query)));
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
        sb.append(value.getText());
        sb.append('\"');
        return sb.toString();
    }
}