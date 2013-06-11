package md.frolov.legume.client.elastic.model.query;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface DateHistogramFacet extends Facet
{
    @AutoBean.PropertyName("date_histogram")
    DateHistogramFacetDef getDateHistogram();

    @AutoBean.PropertyName("date_histogram")
    void setDateHistogram(DateHistogramFacetDef dateHistogram);

    interface DateHistogramFacetDef
    {
        String getField();

        void setField(String field);

        String getInterval();

        /**
         * @param interval The interval allows to set the interval at which buckets will be created for each hit. It allows for the
         *                 constant values of year, quarter, month, week, day, hour, minute.
         *                 <br/>
         *                 It also support time setting like 1.5h (up to w for weeks).
         */
        void setInterval(String interval);
    }
}
