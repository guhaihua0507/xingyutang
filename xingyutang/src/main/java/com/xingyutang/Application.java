package com.xingyutang;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = {"com.xingyutang.app.mapper", "com.xingyutang.rongchuang.mapper", "com.xingyutang.lvcheng.mapper"})
@ComponentScan(basePackages = {"com.xingyutang.**.config", "com.xingyutang.**.controller", "com.xingyutang.**.service", "com.xingyutang.config"})
@EnableScheduling
public class Application {
	public static ApplicationContext applicationContext;

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(Application.class, args);
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return applicationContext.getBean(name, clazz);
	}
}
