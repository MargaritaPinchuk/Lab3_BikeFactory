package bikefactory.storage;

import bikefactory.model.Bicycle;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class BikeMap extends BikeStorage {
    private Map<Integer, Bicycle> bikes = new TreeMap<>();

    @Override
    public boolean add(Bicycle bike) {
        if (bikes.containsKey(bike.getId())) {
            return false;
        }
        bikes.put(bike.getId(), bike);
        return true;
    }

    @Override
    public boolean update(Bicycle bike) {
        if (!bikes.containsKey(bike.getId())) {
            return false;
        }
        bikes.put(bike.getId(), bike);
        return true;
    }

    @Override
    public boolean remove(int id) {
        return bikes.remove(id) != null;
    }

    @Override
    public Bicycle getById(int id) {
        return bikes.get(id);
    }

    @Override
    public Collection<Bicycle> getAll() {
        return bikes.values();
    }

    @Override
    public Iterator<Bicycle> iterator() {
        return bikes.values().iterator();
    }

    @Override
    public void clear() {
        bikes.clear();
    }
}
