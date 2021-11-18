package com.example.reactive.app.webflux.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.BodyExtractors.toFlux;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class RouteController {

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route(GET("/stream3"), this::hello)
                .andRoute(GET("/stream4"), this::stream)
                .andRoute(POST("/echo2"), this::echo)
                .andRoute(POST("/stream"), this::postStream);
    }

    private Mono<ServerResponse> hello(ServerRequest serverRequest) {
        return ok().body(Flux.just("Hello", "World!"), String.class);
    }

    private Mono<ServerResponse> stream(ServerRequest serverRequest) {
        Stream<Integer> stream = Stream.iterate(0, i -> i + 1);
        Flux<Map<String, Integer>> flux = Flux.fromStream(stream.limit(20))
                .map(i -> Collections.singletonMap("value", i));
        return ok().contentType(MediaType.APPLICATION_NDJSON)
                .body(fromPublisher(flux, new ParameterizedTypeReference<Map<String, Integer>>(){}));
    }

    private Mono<ServerResponse> echo(ServerRequest serverRequest) {
        Mono<String> body = serverRequest.bodyToMono(String.class).map(String::toUpperCase);
        return ok().body(body, String.class);
    }

    private Mono<ServerResponse> postStream(ServerRequest serverRequest) {
        Flux<Map<String, Integer>> body = serverRequest.body(
                toFlux(
                        new ParameterizedTypeReference<Map<String, Integer>>(){}
                )
        );

        return ok().contentType(MediaType.TEXT_EVENT_STREAM)
                .body(fromPublisher(body.map(m -> Collections.singletonMap("double", m.get("value") * 2)),
                        new ParameterizedTypeReference<Map<String, Integer>>(){}));
    }
}
