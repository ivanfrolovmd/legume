package md.frolov.legume.client.elastic.model.request;

import java.util.List;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface AndFilter extends Filter
{
    List<Filter> getAnd();
    void setAnd(List<Filter> and);
}
