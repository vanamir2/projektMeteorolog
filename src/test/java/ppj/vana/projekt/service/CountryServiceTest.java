package ppj.vana.projekt.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ppj.vana.projekt.Main;
import ppj.vana.projekt.model.City;
import ppj.vana.projekt.model.Country;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
@ActiveProfiles({"test"})
@TestPropertySource(locations = "classpath:app_test.properties")
public class CountryServiceTest {

    private final Country country1 = new Country("Austrálie");
    private final Country country2 = new Country("Estonsko");
    private final Country country3 = new Country("Norsko");
    private final Country country4 = new Country("Brazílie");
    private final Country country5 = new Country("Česká republika");
    private final City city1 = new City("Sloup v Čechách", country5);
    private final City city2 = new City("Janov", country4);
    private final City city3 = new City("Ostrava", country5);
    @Autowired
    private CountryService countryService;
    @Autowired
    private CityService cityService;

    @Before
    public void init() {
        countryService.deleteAll();
    }

    @Test
    public void testCreateRetrieve() {
        countryService.add(country1);
        List<Country> countries = countryService.getAll();
        System.out.println(countries);

        assertEquals("One field should have been created and retrieved", 1, countries.size());
        assertEquals("Inserted field should match retrieved", country1, countries.get(0));

        countryService.add(country2);
        countryService.add(country3);
        countryService.add(country4);


        countries = countryService.getAll();
        assertEquals("Should be four retrieved fields.", 4, countries.size());
    }

    @Test
    public void testExists() {
        countryService.add(country2);
        countryService.add(country3);
        countryService.add(country4);
        countryService.add(country5);

        assertTrue("Field should exist.", countryService.exists(country2));
        assertFalse("Field should not exist.", countryService.existsById("xkjhsfjlsjf"));
        assertEquals("Česká republika", countryService.get("Česká republika").getName());
    }

    @Test
    public void testCityMapping() {
        countryService.add(country4);
        countryService.add(country5);
        cityService.add(city1);
        cityService.add(city2);
        cityService.add(city3);

        //   EntityGraph<Post> entityGraph = entityManager.createEntityGraph(Post.class);

        //assertEquals( countryService.getByName("Česká republika").get().getCities().size() , 2);
    }
}
