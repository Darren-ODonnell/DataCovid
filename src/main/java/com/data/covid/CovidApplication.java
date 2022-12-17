package com.data.covid;

import com.data.covid.services.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CovidApplication extends SpringBootServletInitializer implements CommandLineRunner  {

    private static final Logger LOG = LoggerFactory.getLogger(CovidApplication.class);

    public static void main(String[] args) {
        LOG.info("LOADING COVID DATA");
        SpringApplication.run(CovidApplication.class, args);
        LOG.info("DATA LOADED");
    }

    @Override
    public void run(String... args) {
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CovidApplication.class);
    }

    @Bean
    public DataLoader schedulerRunner() {
        return new DataLoader();
    }
}
