package md.frolov.legume.client.ui.components;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import md.frolov.legume.client.activities.stream.StreamPlace;
import md.frolov.legume.client.elastic.model.reply.LogEvent;
import md.frolov.legume.client.elastic.query.Search;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.util.ConversionUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogEventFieldComponent extends Composite
{
    private static final RegExp SOLR_SPECIAL_CHARS = RegExp.compile("[-+&|!(){}\\[\\]^\"~*?:\\\\]", "g");
    private static final int MAX_TEXT_LENGTH = 100;

    interface LogEventFieldComponentUiBinder extends UiBinder<Widget, LogEventFieldComponent>
    {
    }

    private static LogEventFieldComponentUiBinder binder = GWT.create(LogEventFieldComponentUiBinder.class);

    @UiField
    Label valueLabel;
    @UiField
    Label keyLabel;
    @UiField
    NavLink includeFilter;
    @UiField
    NavLink includeOnlyFilter;
    @UiField
    NavLink excludeFilter;
    @UiField
    NavLink excludeOnlyFilter;
    @UiField
    FlowPanel container;
    @UiField
    DropdownButton actionsDropdown;
    @UiField
    NavLink copyToClipboard;

    private final Place place;
    private final String queryKey;
    private final Date timestamp;

    private boolean actionLinksAreSet = false;

    public LogEventFieldComponent(String key, String queryKey, Object value, LogEvent logEvent)
    {
        initWidget(binder.createAndBindUi(this));
        keyLabel.setText(key);
        String valueText = getStringValue(value);
        valueLabel.setText(valueText);
        this.queryKey = queryKey;
        place = WidgetInjector.INSTANCE.placeController().getWhere();
        timestamp = logEvent.getTimestamp();
    }

    @UiHandler("actionsDropdown")
    public void onActionsDropdownClick(final ClickEvent event)
    {
        if(actionLinksAreSet) {
            return;
        }
        actionLinksAreSet = true;
        initLinks();
    }

    private void initLinks() {
        String valueText = valueLabel.getText();
        if (valueText != null && valueText.length() > 0 && valueText.length() < MAX_TEXT_LENGTH)
        {
            String filter = getFilter();
            Search query = ((StreamPlace) place).getQuery().clone();
            query.setFocusDate(timestamp);
            String originalQueryString = query.getQuery();
            StreamPlace.Tokenizer tokenizer = new StreamPlace.Tokenizer();

            query.setQuery(getQueryString(originalQueryString, filter));
            includeFilter.setTargetHistoryToken(StreamPlace.TOKEN_PREFIX+":" + tokenizer.getToken(new StreamPlace(query)));
            query.setQuery(getQueryString(originalQueryString, "NOT " + filter));
            excludeFilter.setTargetHistoryToken(StreamPlace.TOKEN_PREFIX+":" + tokenizer.getToken(new StreamPlace(query)));

            Search exclusiveQuery = query.clone();
            exclusiveQuery.setQuery(filter);
            includeOnlyFilter.setTargetHistoryToken(StreamPlace.TOKEN_PREFIX+":"+tokenizer.getToken(new StreamPlace(exclusiveQuery)));
            exclusiveQuery.setQuery(("NOT "+filter));
            excludeOnlyFilter.setTargetHistoryToken(StreamPlace.TOKEN_PREFIX+":"+ tokenizer.getToken(new StreamPlace(exclusiveQuery)));
        }
        else
        {
            includeFilter.setVisible(false);
            excludeFilter.setVisible(false);
            includeOnlyFilter.setVisible(false);
            excludeOnlyFilter.setVisible(false);
        }
    }

    @UiHandler("copyToClipboard")
    public void handleCopyToClipboard(final ClickEvent event)
    {
        event.stopPropagation();

        //TODO move to declarative layout
        Modal modal = new Modal(false, true);
        modal.setWidth("600px");
        modal.setTitle("Copy to clipboard");

        String text = valueLabel.getText();
        TextArea textArea = new TextArea();
        textArea.setWidth("550px");
        textArea.setHeight("300px");
        textArea.setText(text);
        modal.add(textArea);

        modal.show();
        textArea.setSelectionRange(0, text.length());
        textArea.setFocus(true);
    }

    private String getStringValue(Object value)
    {
        String ret = null;
        if (value instanceof Date)
        {
            ret = ConversionUtils.INSTANCE.dateToString((Date) value);
        }
        else if (value instanceof Iterable)
        {
            ret = Joiner.on(", ").skipNulls().join(((Iterable) value).iterator());
        }
        else if (value != null)
        {
            ret = value.toString();
        }

        if (ret == null)
        {
            ret = "";
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
        sb.append(SOLR_SPECIAL_CHARS.replace(valueLabel.getText(), "\\$&"));
        sb.append('\"');

        return sb.toString();
    }
}