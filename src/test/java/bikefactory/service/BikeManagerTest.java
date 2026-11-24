package bikefactory.service;

import bikefactory.model.Bicycle;
import bikefactory.util.ErrorLogger;
import bikefactory.util.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BikeManagerTest {

    private File dataFile;
    private File logFile;
    private BikeManager manager;

    @Before
    public void setUp() throws Exception {
        dataFile = File.createTempFile("bikes-test", ".txt");
        logFile = File.createTempFile("errors-test", ".log");
        dataFile.delete();
        logFile.delete();
        manager = new BikeManager(dataFile.getAbsolutePath(), new ErrorLogger(logFile.getAbsolutePath()));
    }

    @After
    public void tearDown() {
        dataFile.delete();
        logFile.delete();
    }

    private Bicycle bike(int id) {
        Date date = Validator.parseDate("01.01.2020", "дата выпуска");
        return new Bicycle(id, "горный", "Model" + id, 26, 21, 10, date, 100);
    }

    @Test
    public void addBike_newBike_isSavedAndFindable() {
        assertTrue(manager.addBike(bike(1)));
        assertNotNull(manager.findBike(1));
        assertTrue(dataFile.exists());
    }

    @Test
    public void addBike_duplicateId_isRejected() {
        manager.addBike(bike(1));
        assertFalse(manager.addBike(bike(1)));
    }

    @Test
    public void nextId_withExistingBikes_returnsMaxPlusOne() {
        manager.addBike(bike(1));
        manager.addBike(bike(5));
        assertEquals(6, manager.nextId());
    }

    @Test
    public void removeBike_existingId_removesFromManager() {
        manager.addBike(bike(1));
        assertTrue(manager.removeBike(1));
        assertNull(manager.findBike(1));
    }

    @Test
    public void saveAndReload_fromFile_restoresSameData() {
        manager.addBike(bike(1));
        manager.addBike(bike(2));

        BikeManager reloaded = new BikeManager(dataFile.getAbsolutePath(), new ErrorLogger(logFile.getAbsolutePath()));
        reloaded.loadFromFile();

        assertNotNull(reloaded.findBike(1));
        assertNotNull(reloaded.findBike(2));
    }

    @Test
    public void sortList_invalidField_returnsFalse() {
        assertFalse(manager.sortList(99, true));
    }

    @Test
    public void sortList_validField_returnsTrue() {
        manager.addBike(bike(1));
        assertTrue(manager.sortList(1, true));
    }
}
