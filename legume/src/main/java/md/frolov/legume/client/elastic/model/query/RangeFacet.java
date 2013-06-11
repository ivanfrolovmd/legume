package md.frolov.legume.client.elastic.model.query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface RangeFacet extends Facet
{
    Map<String, List<RangeDef>> getRange();

    void setRange(Map<String, List<RangeDef>> range);

    interface RangeDef
    {
    }

    interface RangeStr extends RangeDef
    {
        String getFrom();
        void setFrom(String from);
        String getTo();
        void setTo(String to);
    }

    interface RangeInt extends RangeDef
    {
        int getFrom();
        void setFrom(int from);
        int getTo();
        void setTo(int to);
    }

    interface RangeDate extends RangeDef {
        Date getFrom();
        void setFrom(Date from);
        Date getTo();
        void setTo(Date date);
    }
}
