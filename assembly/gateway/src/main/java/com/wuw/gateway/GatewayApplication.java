package com.wuw.gateway;

import com.wuw.gateway.config.ApplicationStartedEventListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationListener;

import java.util.Set;


@SpringBootApplication(exclude= {
        DataSourceAutoConfiguration.class,
        RedisAutoConfiguration.class, // todo redis
        RedissonAutoConfiguration.class
})
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class GatewayApplication {

    public static void main(String[] args) {

        try {
            // SpringApplication.run(GatewayApplication.class, args);
            SpringApplication app = new SpringApplication(GatewayApplication.class);
            Set<ApplicationListener<?>> ls = app.getListeners();
            ApplicationStartedEventListener asel = new ApplicationStartedEventListener();
            app.addListeners(asel);
            app.run(args);

        }catch (Exception e){
            System.out.println(e.getMessage());
            log.error(e.getMessage());
        }
    }


}
