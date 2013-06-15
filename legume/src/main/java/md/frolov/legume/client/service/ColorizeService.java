package md.frolov.legume.client.service;

import java.util.List;
import java.util.Set;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface ColorizeService
{
    boolean isFieldColorizable(String fieldName);
    void addColorizableField(String fieldName);
    void removeColorizableField(String fieldName);
    Set<String> getColorizableFields();

    String getCssClassName(String fieldName, String value);

    // hue: 0 to 360
    int getColorHue(String fieldName, String value);
    // hue: 0 to 360
    void setColorHue(String fieldName, String value, int hue);

    Set<String> getLabelFields();
    void addLabelField(String fieldName);
    void removeLabelField(String fieldName);
    void clearLabelFields();

    Set<String> getBackgroundFields();
    void addBackgroundField(String fieldName);
    void removeBackgroundField(String fieldName);
    void clearBackgroundFields();

    String getCssColor(String fieldName, String value, int saturation, int light);

    void refresh();
    void saveAndRefresh();

    Set<String> getValues(String fieldName);
}
