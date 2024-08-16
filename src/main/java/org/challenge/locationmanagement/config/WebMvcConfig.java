package org.challenge.locationmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Custom Spring MVC configuration to add support for Pageable.
 *
 * This class configures Spring MVC to automatically resolve Pageable arguments from HTTP request parameters.
 * This enables Spring to handle pagination and sorting of results in endpoints that accept Pageable as an argument.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
    }
}
