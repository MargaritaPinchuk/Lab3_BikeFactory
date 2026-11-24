package bikefactory.storage;

import bikefactory.model.Bicycle;
import bikefactory.util.Validator;
import org.junit.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BikeListTest {

    private Bicycle bike(int id, String model, double weight, double price) {
        Date date = Validator.parseDate("01.01.2020", "дата выпуска");
        return new Bicycle(id, "горный", model, 26, 21, weight, date, price);
    }

    @Test
    public void add_newId_succeeds() {
        BikeList list = new BikeList();
        assertTrue(list.add(bike(1, "A", 10, 100)));
        assertEquals(1, list.size());
    }

    @Test
    public void add_duplicateId_fails() {
        BikeList list = new BikeList();
        list.add(bike(1, "A", 10, 100));
        assertFalse(list.add(bike(1, "B", 12, 200)));
        assertEquals(1, list.size());
    }

    @Test
    public void update_existingId_replacesBike() {
        BikeList list = new BikeList();
        list.add(bike(1, "A", 10, 100));
        assertTrue(list.update(bike(1, "B", 12, 200)));
        assertEquals("B", list.getById(1).getModel());
    }

    @Test
    public void remove_existingId_removesBike() {
        BikeList list = new BikeList();
        list.add(bike(1, "A", 10, 100));
        assertTrue(list.remove(1));
        assertNull(list.getById(1));
    }

    @Test
    public void iterator_visitsAllAddedBikes() {
        BikeList list = new BikeList();
        list.add(bike(1, "A", 10, 100));
        list.add(bike(2, "B", 12, 200));
        Iterator<Bicycle> it = list.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void sort_byPriceAscending_ordersCorrectly() {
        BikeList list = new BikeList();
        list.add(bike(1, "A", 10, 300));
        list.add(bike(2, "B", 12, 100));
        list.add(bike(3, "C", 8, 200));
        list.sort(Comparator.comparingDouble(Bicycle::getPrice));
        Iterator<Bicycle> it = list.iterator();
        assertEquals(100.0, it.next().getPrice(), 0.0001);
        assertEquals(200.0, it.next().getPrice(), 0.0001);
        assertEquals(300.0, it.next().getPrice(), 0.0001);
    }
}
