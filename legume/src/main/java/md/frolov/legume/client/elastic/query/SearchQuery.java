package md.frolov.legume.client.elastic.query;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class SearchQuery implements Query {
    public static final SearchQuery DEFAULT = new SearchQuery();

    private final String query;
    private List<SortOrder> sortOrders = Lists.newArrayList(SortOrder.of("@timestamp",true));
    private int from;
    private int size = 50;

    public SearchQuery()
    {
        this("");
    }

    public SearchQuery(final String query)
    {
        this.query = query;
    }

    @Override
    public String getQueryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/_search?");
        if(!Iterables.isEmpty(sortOrders)) {
            sb.append("sort=");
            sb.append(Joiner.on(',').join(sortOrders));
            sb.append('&');
        }
        sb.append("size=").append(size).append('&');
        sb.append("q=").append(query);
        return sb.toString();
    }

    public String getQuery()
    {
        return query;
    }
}
