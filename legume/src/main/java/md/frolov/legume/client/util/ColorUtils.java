package md.frolov.legume.client.util;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ColorUtils
{
    private static final Logger LOG = Logger.getLogger("ColorUtils");

    public static final int NUMBER_OF_COLORS = 40;
    private static final String BLACK = "rbg(0,0,0)";
    private final List<String> colors;

    public ColorUtils()
    {
        //init colors array
        colors = Lists.newArrayList();
        for (int i = 0; i < NUMBER_OF_COLORS; i++)
        {
            colors.add(getHslColor(1.0 * i / NUMBER_OF_COLORS, 1, 0.4));
        }
    }

    /**
     * Return CssColor based on string hash. This function will always return the same colors for the same strings.
     *
     * @param string arbitrary string.
     * @return hash CssColor
     */
    public String getHashColor(String string, int saturation, int light)
    {
        int hue = Math.abs(string.hashCode())%360;
        return getHslColor(hue, saturation, light);
    }

    /**
     * Returns color in HSL space.
     * @param h hue: 0 to 1.
     * @param s saturation: 0 to 1.
     * @param l light: 0 to 1.
     * @return String in format like: hsl(300,50,80). This can be used in CSS.
     */
    public String getHslColor(double h, double s, double l)
    {
        return getHslColor((int)(h*360),(int)(s*100),(int)(l*100));
    }

    /**
     * Returns color in HSL (Hue Saturation Light) space.
     * @param h hue in degrees: 0 to 360. May be out of bounds
     * @param s saturation in percent: 0 to 100. Greater value means more saturated
     * @param l light in percent: 0 to 100.
     * @return String in format like: hsl(300,50,80)
     */
    public String getHslColor(int h, int s, int l)
    {
        return "hsl("+h+","+s+"%,"+l+"%)";
    }
}
