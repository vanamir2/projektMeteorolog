package ppj.vana.projekt.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.Expose;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ppj.vana.projekt.model.serialization.ObjectIdSerializer;
import ppj.vana.projekt.service.UtilService;

import javax.persistence.Id;
import java.util.Map;

@Document(collection = "meteorolog")
public class Measurement {

    @Id
    @Expose
    //@JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;

    @Indexed
    private Integer cityID;

    private Long timeOfMeasurement;

    private Double temperature;

    private Integer humidity;

    private Integer pressure;

    // wind speed in m/s
    private Double wind;

    public Measurement() {
    }

    public Measurement(ObjectId id, Integer cityID, Long timeOfMeasurement, Double temperature, Integer humidity, Integer pressure, Double wind) {
        this.id = id;
        this.cityID = cityID;
        this.timeOfMeasurement = timeOfMeasurement;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.wind = wind;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getCityID() {
        return cityID;
    }

    public void setCityID(Integer cityID) {
        this.cityID = cityID;
    }

    public Long getTimeOfMeasurement() {
        return timeOfMeasurement;
    }

    public void setTimeOfMeasurement(Long timeOfMeasurement) {
        this.timeOfMeasurement = timeOfMeasurement;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Double getWind() {
        return wind;
    }

    public void setWind(Double wind) {
        this.wind = wind;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", cityID=" + cityID +
                ", timeOfMeasurement=" + timeOfMeasurement +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", wind=" + wind +
                '}';
    }

    /**
     * mapIdToCity can be obratined from cityService.getIdToCityMap();
     * */
    public String toStringReadable(Map<Integer, City> mapIdToCity ) {
        return "City: " + mapIdToCity.get(cityID).getName() +
                ", time=" + UtilService.timestampToStringSeconds(timeOfMeasurement) +
                ", temperature=" + temperature + " °C" +
                ", humidity=" + humidity + "%" +
                ", pressure=" + pressure + " hPa" +
                ", wind=" + wind + " m/s";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        if (!id.equals(that.id)) return false;
        if (cityID != null ? !cityID.equals(that.cityID) : that.cityID != null) return false;
        if (timeOfMeasurement != null ? !timeOfMeasurement.equals(that.timeOfMeasurement) : that.timeOfMeasurement != null)
            return false;
        if (temperature != null ? !temperature.equals(that.temperature) : that.temperature != null) return false;
        if (humidity != null ? !humidity.equals(that.humidity) : that.humidity != null) return false;
        if (pressure != null ? !pressure.equals(that.pressure) : that.pressure != null) return false;
        return wind != null ? wind.equals(that.wind) : that.wind == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (cityID != null ? cityID.hashCode() : 0);
        result = 31 * result + (timeOfMeasurement != null ? timeOfMeasurement.hashCode() : 0);
        result = 31 * result + (temperature != null ? temperature.hashCode() : 0);
        result = 31 * result + (humidity != null ? humidity.hashCode() : 0);
        result = 31 * result + (pressure != null ? pressure.hashCode() : 0);
        result = 31 * result + (wind != null ? wind.hashCode() : 0);
        return result;
    }
}
