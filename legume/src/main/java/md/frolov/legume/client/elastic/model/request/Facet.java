package md.frolov.legume.client.elastic.model.request;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface Facet
{
    SearchQuery getFilter();
    void setFilter(SearchQuery filter);
}
