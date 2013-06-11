package md.frolov.legume.client.elastic.model.query;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface FilteredQuery extends Query
{
    FilteredQueryDef getFiltered();
    void setFiltered(FilteredQueryDef filtered);

    interface FilteredQueryDef
    {
        Query getQuery();
        void setQuery(Query query);

        Filter getFilter();
        void setFilter(Filter filter);
    }
}
