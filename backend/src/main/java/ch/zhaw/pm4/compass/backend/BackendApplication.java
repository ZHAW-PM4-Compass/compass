package ch.zhaw.pm4.compass.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }) 
public class BackendApplication {

	public static void main(String[] args) {
		//Persistence.createEntityManagerFactory("MyPersistenceUnit");
    SpringApplication.run(BackendApplication.class, args);
	}

}
