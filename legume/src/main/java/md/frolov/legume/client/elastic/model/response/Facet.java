package md.frolov.legume.client.elastic.model.response;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface Facet
{
    @AutoBean.PropertyName("_type")
    String getType();

    List<Range> getRanges();

    interface Range{
        long getFrom();
        long getTo();
        long getCount();
    }
}
