package md.frolov.legume.client.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.gwt.canvas.dom.client.CssColor;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ColorUtils
{
    private static final Logger LOG = Logger.getLogger("ColorUtils");

    public static final int NUMBER_OF_COLORS = 40;
    private static final String BLACK = CssColor.make(0, 0, 0).value();
    private final List<String> colors;
    private final LoadingCache<String, String> stringColorCache;

    public ColorUtils()
    {
        //init colors array
        colors = Lists.newArrayList();
        for (int i = 0; i < NUMBER_OF_COLORS; i++)
        {
            colors.add(getHslColor(1.0 * i / NUMBER_OF_COLORS, 1, 0.4).value());
        }

        stringColorCache = CacheBuilder.newBuilder().maximumSize(100).build(new CacheLoader<String, String>()
        {
            @Override
            public String load(final String key) throws Exception
            {
                if (key == null)
                {
                    return BLACK;
                }

                double hashCode = Math.abs(key.hashCode());
                return getColor(hashCode % NUMBER_OF_COLORS / NUMBER_OF_COLORS);
            }
        });
    }

    /**
     * Return CssColor based on gamma function.
     *
     * @param gamma double value 0 to 1.
     * @return hash CssColor
     */
    public String getColor(double gamma)
    {
        if (gamma < 0 || gamma > 1)
        {
            throw new IllegalArgumentException("Gamma must be within 0 and 1");
        }

        int ix = (int) (gamma * NUMBER_OF_COLORS);
        return Iterables.get(colors, ix, BLACK);
    }

    /**
     * Return CssColor based on string hash. This function will always return the same colors for the same strings.
     *
     * @param string arbitrary string.
     * @return hash CssColor
     */
    public String getColor(String string)
    {
        try
        {
            return stringColorCache.getUnchecked(string);
        }
        catch (UncheckedExecutionException e)
        {
            LOG.log(Level.WARNING, "Can't fetch color", e);
            return BLACK;
        }
    }

    private CssColor getHslColor(double h, double s, double l)
    {
        double r;
        double g;
        double b;

        if (s == 0)
        {
            r = g = b = l; // achromatic
        }
        else
        {

            double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2. * l - q;
            r = hue2rgb(p, q, h + 1. / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1. / 3);
        }

        return CssColor.make((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    private static double hue2rgb(double p, double q, double t)
    {
        if (t < 0)
        {
            t += 1;
        }
        if (t > 1)
        {
            t -= 1;
        }
        if (t < 1. / 6)
        {
            return p + (q - p) * 6 * t;
        }
        if (t < 1. / 2)
        {
            return q;
        }
        if (t < 2. / 3)
        {
            return p + (q - p) * (2. / 3 - t) * 6;
        }
        return p;
    }
}
