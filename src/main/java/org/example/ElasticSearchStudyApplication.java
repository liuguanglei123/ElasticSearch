package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-06-18-21:10
 * @Description:
 */
@SpringBootApplication
public class ElasticSearchStudyApplication extends SpringBootServletInitializer {

    public static void main(final String[] args) {
        SpringApplication.run(ElasticSearchStudyApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(ElasticSearchStudyApplication.class);
    }

}
