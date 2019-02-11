package cab.management.CabManagementSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@ComponentScan("cab.management.CabManagementSystem")
//@PropertySource("application.properties")
@EnableJpaAuditing
public class CabManagementSystemApplication {

	private static final Logger logger = LoggerFactory.getLogger(CabManagementSystemApplication.class);

	public static void main(String[] args) {
		logger.info("Enter Into The Cab Management System");
		SpringApplication.run(CabManagementSystemApplication.class, args);
	}

}
