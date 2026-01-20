package com.stockManagePortfolio.stockManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Main entry point for the Stock Management System application
// Enables component scanning, JPA repositories, and entity scanning across all packages starting with 'com'
@SpringBootApplication(scanBasePackages = "com")
@EnableJpaRepositories(basePackages = "com")
@EntityScan(basePackages = "com")
public class StockManagerApplication {

	public static void main(String[] args) {
		// Start the Spring Boot application
		SpringApplication.run(StockManagerApplication.class, args);
	}

}
