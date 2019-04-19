package com.duocode.assigment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.duocode.assigment.helper.PrismicConfig;

@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) {		
		
		SpringApplication.run(DemoApplication.class, args);
	}

}
