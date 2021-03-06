package md.frolov.legume.client.elastic.model.reply;

import java.util.Date;
import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface Facet
{
    @AutoBean.PropertyName("_type")
    String getType();

    long getTotal();
    long getMissing();
    long getOther();

    List<Range> getRanges();

    List<Entry> getEntries();

    List<Term> getTerms();

    interface Range{
        long getFrom();
        long getTo();
        long getCount();
    }

    interface Entry{
        Date getTime();
        long getCount();
    }

    interface Term {
        String getTerm();
        long getCount();
    }
}
