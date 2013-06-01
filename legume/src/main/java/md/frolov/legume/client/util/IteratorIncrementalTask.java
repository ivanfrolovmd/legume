package md.frolov.legume.client.util;

import java.util.Iterator;

import com.google.gwt.core.client.Scheduler;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public abstract class IteratorIncrementalTask<T> implements Scheduler.RepeatingCommand
{
    private final Iterator<T> collection;
    private boolean started = false;

    protected IteratorIncrementalTask(final Iterable<T> collection)
    {
        this.collection = collection.iterator();
    }

    @Override
    public final boolean execute()
    {
        if(!started) {
            beforeAll();
            started = true;
        }

        if (collection.hasNext())
        {
            T obj = collection.next();
            iterate(obj);
        }

        if (!collection.hasNext())
        {
            afterAll();
        }

        return collection.hasNext();
    }

    public abstract void iterate(T obj);

    public void beforeAll()
    {
    }

    public void afterAll()
    {
    }
}

