package md.frolov.legume.client.elastic.model.request;

import java.util.Map;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface RangeFilter extends Filter
{
    Map<String, RangeFilterDef> getRange();
    void setRange(Map<String, RangeFilterDef> range);

    interface RangeFilterDef {
        String getFrom();
        void setFrom(String from);

        String getTo();
        void setTo(String to);

        @AutoBean.PropertyName("include_lower")
        boolean isIncludeLower();
        @AutoBean.PropertyName("include_lower")
        void setIncludeLower(boolean includeLower);

        @AutoBean.PropertyName("include_upper")
        boolean isIncludeUpper();
        @AutoBean.PropertyName("include_upper")
        void setIncludeUpper(boolean includeUpper);
    }
}
