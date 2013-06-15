package md.frolov.legume.client.model;

import java.util.List;
import java.util.Map;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ColorsConf
{
    Map<String, Map<String, Integer>> getFieldValueColorMap();
    void setFieldValueColorMap(Map<String, Map<String, Integer>> fieldValueColorMap);

    List<String> getLabelFields();
    void setLabelFields(List<String> labelFields);

    List<String> getBackgroundFields();
    void setBackgroundFields(List<String> backgroundFields);
}

