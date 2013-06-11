package md.frolov.legume.client.elastic.model.query;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

import md.frolov.legume.client.elastic.model.ModelFactory;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class QueryStringCategory
{
    //TODO probably this is not required
    public static QueryString clone(AutoBean<QueryString> queryString){
        Splittable data = AutoBeanCodex.encode(queryString);
        return AutoBeanCodex.decode(ModelFactory.INSTANCE,QueryString.class,data).as();
    }
}
