package com.stockManagePortfolio.stockManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

// Main entry point for the Stock Management System application
// Enables component scanning, JPA repositories across all packages starting with 'com'
@SpringBootApplication(scanBasePackages = "com")
@EntityScan(basePackages = "com")
@EnableJpaRepositories(basePackages = "com")
@EnableScheduling
public class StockManagerApplication {

	public static void main(String[] args) {
		// Start the Spring Boot application
		SpringApplication.run(StockManagerApplication.class, args);
	}

}
