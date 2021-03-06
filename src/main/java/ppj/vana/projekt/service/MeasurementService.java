package ppj.vana.projekt.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ppj.vana.projekt.model.City;
import ppj.vana.projekt.model.Measurement;
import ppj.vana.projekt.model.repository.MeasurementRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class MeasurementService implements IService<Measurement, ObjectId> {

    private static final Long MAX_LENGTH_OF_MEASUREMENT = 730L; // 2 years
    private static final Logger logger = LoggerFactory.getLogger(MeasurementService.class);
    private static final String AVG_TEMP = "avgTemp";
    private static final String AVG_HUM = "avgHum";
    private static final String AVG_PRESS = "avgPress";
    private static final String AVG_WIND = "avgWind";
    private final MongoOperations mongo;
    private final MeasurementRepository measurementRepository;
    private final CityService cityService;
    @Value("${app.daysToExpire}")
    private Integer daysToExpire;

    public MeasurementService(MongoOperations mongo, MeasurementRepository measurementRepository, CityService cityService) {
        this.mongo = mongo;
        this.measurementRepository = measurementRepository;
        this.cityService = cityService;
    }

    // ---------------------------------------------------------------- CUSTOM PUBLIC METHODS
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Measurement> findAllRecordForCities(List<Integer> citiesID) {
        return mongo.find(Query.query(where("cityID").in(citiesID)), Measurement.class);
    }

    /**
     * Returns instance of Measurement with average values.
     *
     * @param cityName the city name
     * @param days     how many days backward it can search. Range 1-365.
     * @return the string
     */
    public String averageValuesForCity(String cityName, int days) {
        // city does not exists? error
        if (!cityService.existsById(cityName))
            return "City " + cityName + " does not exist.";

        City city = cityService.get(cityName);
        // city does not have connection with mongoDB? error
        Integer cityID = city.getOpenWeatherMapID();
        if (cityID == null)
            return "City " + cityName + " does not have any measured model.";

        // range is 1-730, otherwise null
        if (days < 1 || days > MAX_LENGTH_OF_MEASUREMENT)
            return "You can calculate average back to 1-730 days.";

        // timestamp x days back
        Long timestampSeconds = UtilService.getTimestampXDaysBackInSeconds(days);
        Document avgMeasurement = this.getAverageAfterTimestamp(cityID, timestampSeconds);
        String output = String.format("Average values for %s in %d last days:", cityName, days) + System.lineSeparator();
        output += this.formatAverageValues(avgMeasurement);
        logger.info(output);
        return output;
    }

    // calculate average values for selected city
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Document getAverageValuesForCity(Integer cityID) {
        return this.getAverageAfterTimestamp(cityID, MAX_LENGTH_OF_MEASUREMENT);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Measurement> findAllRecordForCityID(Integer cityID) {
        return mongo.find(Query.query(where("cityID").is(cityID)), Measurement.class);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Measurement getByHexaString(String id) {
        ObjectId objectId = new ObjectId(id);
        return mongo.findOne(Query.query(where("_id").is(objectId)), Measurement.class);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Boolean existsByHexaString(String id) {
        ObjectId objectId = new ObjectId(id);
        return measurementRepository.existsById(objectId);
    }

    @Transactional
    public void update(Measurement measurement) {
        measurementRepository.save(measurement);
    }

    /**
     * cityID - city
     * timestampSeconds - select only data measured after this timepstamp (newer ones)
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Measurement> getMeasurementsForCityAfterTimestamp(Integer cityID, Long timestampSeconds) {
        return mongo.find(Query.query(where("cityID").is(cityID).and("timeOfMeasurement").gt(timestampSeconds)), Measurement.class);
    }

    /**
     * timestampSeconds - select only data measured before this timepstamp (newer ones)
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Measurement> getMeasurementBeforeTimestamp(Long timestampSeconds) {
        return mongo.find(Query.query(where("timeOfMeasurement").lt(timestampSeconds)), Measurement.class);
    }

    // ------------------------------------------------ INTERFACE @Override
    @Override
    public List<Measurement> getAll() {
        return measurementRepository.findAll();
    }

    @Override
    public boolean exists(Measurement measurement) {
        return measurementRepository.existsById(measurement.getId());
    }

    @Override
    public Measurement get(ObjectId objectId) {
        return mongo.findOne(Query.query(where("_id").is(objectId)), Measurement.class);
    }

    @Override
    public long count() {
        return measurementRepository.count();
    }

    @Override
    public void deleteAll() {
        measurementRepository.deleteAll();
    }

    @Override
    public void add(Measurement entity) {
        entity.setTtl(Date.from(LocalDateTime.now().plusDays(daysToExpire).toInstant(ZoneOffset.ofHours(2))));
        mongo.insert(entity);
    }

    @Override
    public void delete(Measurement measurement) {
        mongo.remove(measurement);
    }

    // ------------------------------------------------ PRIVATE METHODS

    private String formatAverageValues(Document averageData) {
        String output = String.format("Temperature: %.1f", averageData.get(AVG_TEMP)) + System.lineSeparator();
        output += String.format("Humidity: %.1f", averageData.get(AVG_HUM)) + System.lineSeparator();
        output += String.format("Pressure: %.1f", averageData.get(AVG_PRESS)) + System.lineSeparator();
        output += String.format("Wind speed: %.1f", averageData.get(AVG_WIND)) + System.lineSeparator();
        return output;
    }

    // calculate average values for selected city, there is no check of cityID - that should be done before calling this mathod.
    private Document getAverageAfterTimestamp(Integer cityID, Long timestampSeconds) {
        if (cityID == null) {
            logger.error("Method readAverage(Integer cityID) was called with no cityID filled.");
            throw new NullPointerException("Method readAverage(Integer cityID) was called with no cityID filled.");
        }

        MatchOperation matchStage = Aggregation.match(new Criteria("cityID").is(cityID).and("timeOfMeasurement").gte(timestampSeconds));
        ProjectionOperation projection = Aggregation.project("temperature", "humidity", "pressure", "wind", "cityID");
        GroupOperation group = Aggregation.group("cityID")
                .avg("temperature").as(AVG_TEMP)
                .avg("humidity").as(AVG_HUM)
                .avg("pressure").as(AVG_PRESS)
                .avg("wind").as(AVG_WIND);
        // ORDER OF PARAMETERS MATTER!
        TypedAggregation<Measurement> aggregation = Aggregation.newAggregation(Measurement.class, matchStage, projection, group);
        List<Document> list = mongo.aggregate(aggregation, Document.class).getMappedResults();
        if (list == null)
            throw new NullPointerException("There is no city with id " + cityID + " method getAverageAfterTimestamp() failed.");
        else if (list.isEmpty())
            throw new IllegalStateException("There is no city with id " + cityID + " method getAverageAfterTimestamp() failed.");
        return list.get(0);
    }

    // ------------------------------------------------ MAP-REDUCE

    //Map-reduce is a model processing paradigm for condensing large volumes of model into useful aggregated results.
    // For map-reduce operations, MongoDB provides the mapReduce database command.
    public Map<Integer, Integer> numOfRecordsUsingMapReduce() {
        final String mapJS = "classpath:mongoDB/measurement_cityID_map.js";
        final String reduceJS = "classpath:mongoDB/measurement_cityID_reduce.js";
        // query , collection (something like table in RDBMS), map, reduce, entityClass
        MapReduceResults<CountEntry> mapReduceResult = mongo.mapReduce(new Query(), mongo.getCollectionName(Measurement.class), mapJS, reduceJS, CountEntry.class);
        Map<Integer, Integer> numOfRecords = new TreeMap<>();
        mapReduceResult.forEach((result) -> numOfRecords.put(result.id, result.value));
        return numOfRecords;
    }

    private static class CountEntry {
        // id == cityID -- this is defined in map
        public int id; // THIS MUST BE EQUALS TO ID OF ENTITY (Measurement), othwerwise it gets fucked
        public int value; // count
    }

}
