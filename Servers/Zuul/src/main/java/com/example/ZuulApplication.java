package com.example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
@CrossOrigin(origins={"http://ec2-3-229-134-85.compute-1.amazonaws.com", "http://ec2-3-229-134-85.compute-1.amazonaws.com:10001", "http://ec2-3-229-134-85.compute-1.amazonaws.com:10000"},  maxAge=3600, allowCredentials="true", allowedHeaders="*")
public class ZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}
	
	@Bean
	public CorsFilter corsFilter() {
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    final CorsConfiguration config = new CorsConfiguration();
	    List<String> allowedOrigins = new ArrayList<>();
	    allowedOrigins.add("http://ec2-3-229-134-85.compute-1.amazonaws.com");
	    allowedOrigins.add("http://ec2-3-229-134-85.compute-1.amazonaws.com:9015");
	    allowedOrigins.add("http://ec2-3-229-134-85.compute-1.amazonaws.com:10000");
	    allowedOrigins.add("http://ec2-3-229-134-85.compute-1.amazonaws.com:10001");
	    config.setAllowCredentials(true);
	    config.setAllowedOrigins(allowedOrigins);
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("OPTIONS");
	    config.addAllowedMethod("HEAD");
	    config.addAllowedMethod("GET");
	    config.addAllowedMethod("PUT");
	    config.addAllowedMethod("POST");
	    config.addAllowedMethod("DELETE");
	    config.addAllowedMethod("PATCH");
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}

}
