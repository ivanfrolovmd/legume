package md.frolov.legume.client.elastic.model.query;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface Facet
{
    @AutoBean.PropertyName("facet_filter")
    Filter getFilter();
    @AutoBean.PropertyName("facet_filter")
    void setFilter(Filter filter);
}
