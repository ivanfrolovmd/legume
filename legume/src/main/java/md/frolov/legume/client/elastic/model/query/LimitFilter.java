package md.frolov.legume.client.elastic.model.query;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface LimitFilter extends Filter
{
    LimitFilterDef getLimit();
    void setLimit(LimitFilterDef filter);

    interface LimitFilterDef {
        long getValue();
        void setValue(long value);
    }
}
