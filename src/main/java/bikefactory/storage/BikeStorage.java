package bikefactory.storage;

import bikefactory.model.Bicycle;

import java.util.Collection;
import java.util.Iterator;

public abstract class BikeStorage {
    public abstract boolean add(Bicycle bike);

    public abstract boolean update(Bicycle bike);

    public abstract boolean remove(int id);

    public abstract Bicycle getById(int id);

    public abstract Collection<Bicycle> getAll();

    public abstract Iterator<Bicycle> iterator();

    public abstract void clear();

    public int size() {
        return getAll().size();
    }

    public boolean contains(int id) {
        return getById(id) != null;
    }
}
