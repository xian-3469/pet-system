package com.ly.pet;

import com.ly.pet.utils.PathUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetManagerApplication {

    public static void main(String[] args) {
        System.out.println("Project Path : " + PathUtils.getClassLoadRootPath());
        SpringApplication.run(PetManagerApplication.class, args);
    }

}
