package md.frolov.legume.client.model;

import java.util.Map;
import java.util.Set;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ColorsConf
{
    Map<String, Map<String, Integer>> getFieldValueColorMap();
    void setFieldValueColorMap(Map<String, Map<String, Integer>> fieldValueColorMap);

    Set<String> getLabelFields();
    void setLabelFields(Set<String> labelFields);

    Set<String> getBackgroundFields();
    void setBackgroundFields(Set<String> backgroundFields);
}

