package ppj.vana.projekt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import ppj.vana.projekt.dao.provisioner.SqlProvisioner;
import ppj.vana.projekt.service.*;

@SpringBootApplication
@EnableJpaRepositories("ppj.vana.projekt")
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Autowired
    private MongoTemplate mongo;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Main.class, args);

        CityService cityService = ctx.getBean(CityService.class);
        CountryService countryService = ctx.getBean(CountryService.class);
        MongoMeasurementService measurementService = ctx.getBean(MongoMeasurementService.class);
        WeatherDownloaderService weatherDownloaderService = ctx.getBean(WeatherDownloaderService.class);

        // ctxProvider
        System.out.println(CtxProvider.getContext().getBean(CountryService.class).getAll().toString());

        // load curret measurement for all cities
        //weatherDownloaderService.loadWeatherToDatabase(cityService.getAll());

        // docasna testovaci class
        ctx.getBean(Foo.class).makeSound();
    }

    @Bean
    public MongoMeasurementService mongoUserService() {
        return new MongoMeasurementService(mongo);
    }

    @Profile({"devel"})
    @Bean(initMethod = "doProvision")
    public SqlProvisioner provisioner() {
        return new SqlProvisioner();
    }

}