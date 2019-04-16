package com.concretepage;
import com.concretepage.storage.StorageProperties;
import com.concretepage.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class MyApplication {  
	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
    }    
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
            storageService.deleteAll();
            storageService.init();
		};
	}
}            