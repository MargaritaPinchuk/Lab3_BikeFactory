package bikefactory.storage;

import bikefactory.model.Bicycle;
import bikefactory.util.Validator;
import org.junit.Test;

import java.util.Date;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BikeMapTest {

    private Bicycle bike(int id, String model) {
        Date date = Validator.parseDate("01.01.2020", "дата выпуска");
        return new Bicycle(id, "горный", model, 26, 21, 10, date, 100);
    }

    @Test
    public void add_newId_succeeds() {
        BikeMap map = new BikeMap();
        assertTrue(map.add(bike(5, "A")));
        assertEquals(1, map.size());
    }

    @Test
    public void add_duplicateId_fails() {
        BikeMap map = new BikeMap();
        map.add(bike(5, "A"));
        assertFalse(map.add(bike(5, "B")));
    }

    @Test
    public void iterator_returnsIdsInAscendingOrder() {
        BikeMap map = new BikeMap();
        map.add(bike(5, "A"));
        map.add(bike(1, "B"));
        map.add(bike(3, "C"));
        Iterator<Bicycle> it = map.iterator();
        assertEquals(1, it.next().getId());
        assertEquals(3, it.next().getId());
        assertEquals(5, it.next().getId());
    }

    @Test
    public void remove_existingId_removesBike() {
        BikeMap map = new BikeMap();
        map.add(bike(5, "A"));
        assertTrue(map.remove(5));
        assertNull(map.getById(5));
    }
}
