package com.wuw.ucenter.server;

import com.wuw.ucenter.server.config.ApplicationStartedEventListener;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@SpringBootApplication(exclude= {
		DataSourceAutoConfiguration.class,
		//RedisAutoConfiguration.class, // todo redis
		//RedissonAutoConfiguration.class
})
@EnableDiscoveryClient

@ComponentScan(basePackages = {"com.wuw", "com.baidu.fsg"})
@MapperScan("com.wuw.ucenter.server.dao")
@Slf4j
public class UcenterServerApplication {

	public static void main(String[] args) {
		try {
			// SpringApplication.run(UcenterServerApplication.class, args);

			SpringApplication app = new SpringApplication(UcenterServerApplication.class);
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
