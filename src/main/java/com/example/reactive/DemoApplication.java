package com.example.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

//	@Bean
//	RouterFunction<ServerResponse> routes() {
//		return route(GET("/route"),
//				request -> ok().body(Flux.just("Hello", "Web", "Flux"), String.class));
//	}
}
