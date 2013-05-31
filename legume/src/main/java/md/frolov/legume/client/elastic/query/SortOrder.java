package md.frolov.legume.client.elastic.query;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SortOrder
{
    private final String fieldName;
    private final boolean ascending;

    public static SortOrder of(String fieldName, boolean ascending) {
        return new SortOrder(fieldName, ascending);
    }

    private SortOrder(final String fieldName, final boolean ascending)
    {
        this.fieldName = fieldName;
        this.ascending = ascending;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public boolean isAscending()
    {
        return ascending;
    }

    @Override
    public String toString()
    {
        return fieldName + ":"+ (ascending?"asc":"desc");
    }
}
