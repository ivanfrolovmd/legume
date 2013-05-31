package md.frolov.legume.client.elastic.query;

public class SearchQuery {
    public final String queryString;

    public SearchQuery(final String queryString)
    {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }
}
