package md.frolov.legume.jettyrunner;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class StringServlet extends HttpServlet
{
    private final String contents;

    public StringServlet(final String contents)
    {
        this.contents = contents;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        IOUtils.write(contents, resp.getOutputStream());
    }
}
