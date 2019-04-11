package ppj.vana.projekt.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ppj.vana.projekt.Main;
import ppj.vana.projekt.data.Measurement;
import ppj.vana.projekt.repositories.MeasurementRepository;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
@ActiveProfiles({"test"})
public class MongoMeasurementServiceTest {

    private final Measurement measurement1 = new Measurement(3077929, 123, 20);
    private final Measurement measurement2 = new Measurement(3077929, 124, 20);
    private final Measurement measurement3 = new Measurement(3077929, 999, 28);

    @Autowired
    private MongoMeasurementService measurementService;
    @Autowired
    private MeasurementRepository measurementRepository;

    @Before
    public void init() {
        measurementRepository.deleteAll();
    }

    @Test
    public void testCreateRetrieve() {
        assertEquals("Should be 0 retrieved fields.", 0, measurementRepository.count());
        measurementService.add(measurement1);
        measurementService.add(measurement2);
        measurementService.add(measurement3);
        assertEquals("Should be 3 retrieved fields.", 3, measurementRepository.count());
        measurementService.remove(measurement2);
        assertEquals("Should be 2 retrieved fields.", 2, measurementRepository.count());
        assertEquals("Should be equal", 28, measurementService.find(measurement3.getId()).getTemperature().intValue());

        List<Measurement> measurementList = measurementService.findAllRecordForCityID(3077929);
        assertEquals("Should be equal", 2, measurementList.size());
    }
}