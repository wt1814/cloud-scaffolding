package com.wuw.common.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommonServerApplication {

    public static void main(String[] args) {

        try{
            SpringApplication.run(CommonServerApplication.class, args);
        }catch (Exception e){
            System.out.println(e);
        }
    }

}
