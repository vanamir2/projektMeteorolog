package ppj.vana.projekt.server.controllers.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ppj.vana.projekt.data.Country;
import ppj.vana.projekt.server.ServerAPI;
import ppj.vana.projekt.server.controllers.exceptions.APIErrorMessage;
import ppj.vana.projekt.server.controllers.exceptions.APIException;
import ppj.vana.projekt.service.CountryService;

import java.util.List;

@RestController
@RequestMapping("/country")
// TODO - opravit "value" u requestmappingu - anotace nad class to zjednodušší, ale aktuálně to není impplementováno.
public class CountryRESTController {

    @Autowired
    private CountryService countryService;

    // GET - všechny státy JSON formát
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<Country>> getCountries() {
        List<Country> countries = countryService.getAll();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

    // GET - by ID (nazevStatu)
    @RequestMapping(value = ServerAPI.COUNTRY_PATH_NAME, method = RequestMethod.GET)
    public ResponseEntity<Country> getCountryByID(@PathVariable(ServerAPI.COUNTRY_NAME) String countryName) {
        Country country = countryService.getByName(countryName).orElse(null);
        if (country == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    // DELETE - by ID (nazevStatu)
    @RequestMapping(value = ServerAPI.COUNTRY_PATH_NAME, method = RequestMethod.DELETE)
    public ResponseEntity deleteCountry(@PathVariable(ServerAPI.COUNTRY_NAME) String countryName) {
        Country country = countryService.getByName(countryName).orElse(null);
        if (country == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            countryService.delete(country);
        return new ResponseEntity(HttpStatus.OK);
    }

    // PUT - add Country
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity addCountry(@RequestBody Country country) {
        if (countryService.exists(country.getName()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        countryService.save(country);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIErrorMessage> handleAPIException(APIException ex) {
        return new ResponseEntity<>(new APIErrorMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


}