package md.frolov.legume.client.elastic.model.request;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface TermFacet extends Facet
{
    TermsDef getTerms();
    void setTerms(TermsDef termsDef);

    interface TermsDef {
        String getField();
        void setField(String field);
        int getSize();
        void setSize(int size);
        Order getOrder();
        void setOrder(Order order);
    }

    enum Order {
        count, term, reverse_count, reverse_term
    }
}
