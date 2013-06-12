package md.frolov.legume.client.elastic.model.query;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface TermFacet extends Facet
{
    TermsDef getTerms();
    void setTerms(TermsDef termsDef);

    interface TermsDef {
        String getField();
        void setField(String field);
        @AutoBean.PropertyName("script_field")
        String getScriptField();
        @AutoBean.PropertyName("script_field")
        void setScriptField(String scriptField);
        String getScript();
        void setScript(String script);
        int getSize();
        void setSize(int size);
        Order getOrder();
        void setOrder(Order order);
    }

    enum Order {
        count, term, reverse_count, reverse_term
    }
}
