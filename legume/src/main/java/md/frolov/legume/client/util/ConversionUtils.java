package md.frolov.legume.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ConversionUtils
{
    public static final ConversionUtils INSTANCE = new ConversionUtils();

    private static final DateTimeFormat DTF = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public String dateToString(Date date) {
        return DTF.format(date);
    }

    public String dateToString(Date date, TimeZone timeZone) {
        return DTF.format(date, timeZone);
    }
}
