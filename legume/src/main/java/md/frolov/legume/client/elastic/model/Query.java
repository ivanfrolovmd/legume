package md.frolov.legume.client.elastic.model;

public class Query {
    public final String queryString;

    public Query(final String queryString)
    {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }
}
