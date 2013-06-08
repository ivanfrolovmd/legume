package md.frolov.legume.client.elastic.model.request;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface QueryFilter extends Filter
{
    Query getQuery();
    void setQuery(Query query);
}
