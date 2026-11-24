package bikefactory.storage;

import bikefactory.model.Bicycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class BikeList extends BikeStorage {
    private List<Bicycle> bikes = new ArrayList<>();

    @Override
    public boolean add(Bicycle bike) {
        if (contains(bike.getId())) {
            return false;
        }
        bikes.add(bike);
        return true;
    }

    @Override
    public boolean update(Bicycle bike) {
        for (int i = 0; i < bikes.size(); i++) {
            if (bikes.get(i).getId() == bike.getId()) {
                bikes.set(i, bike);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(int id) {
        Bicycle bike = getById(id);
        if (bike == null) {
            return false;
        }
        return bikes.remove(bike);
    }

    @Override
    public Bicycle getById(int id) {
        for (Bicycle bike : bikes) {
            if (bike.getId() == id) {
                return bike;
            }
        }
        return null;
    }

    @Override
    public Collection<Bicycle> getAll() {
        return bikes;
    }

    @Override
    public Iterator<Bicycle> iterator() {
        return bikes.iterator();
    }

    @Override
    public void clear() {
        bikes.clear();
    }

    public void sort(Comparator<Bicycle> comparator) {
        Collections.sort(bikes, comparator);
    }
}
