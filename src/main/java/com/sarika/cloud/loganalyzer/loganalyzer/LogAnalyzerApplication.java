package com.sarika.cloud.loganalyzer.loganalyzer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class LogAnalyzerApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogAnalyzerApplication.class, args);
	}
}
