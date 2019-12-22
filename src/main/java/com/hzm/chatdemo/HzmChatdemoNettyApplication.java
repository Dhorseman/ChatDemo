package com.hzm.chatdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.hzm..chatdemo.mapper")
@ComponentScan(basePackages = {"com.hzm","org.n3r.idworker"})
public class HzmChatdemoNettyApplication {

	public static void main(String[] args) {
		SpringApplication.run(HzmChatdemoNettyApplication.class, args);
	}

}
