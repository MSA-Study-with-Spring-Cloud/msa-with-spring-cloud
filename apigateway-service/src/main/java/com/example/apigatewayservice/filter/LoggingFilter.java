package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
//        return (exchange, chain) ->{
//            ServerHttpRequest request=exchange.getRequest();
//            ServerHttpResponse response=exchange.getResponse();
//
//            log.info("Global Filter baseMessage: {}", config.getBaseMessage());
//
//            if(config.isPreLogger()){
//                log.info("Global Filter Start: request id->{}",request.getId());
//            }
//
//            //Custom Post Filter
//            return chain.filter(exchange).then(Mono.fromRunnable(()-> { //Mono라는 객체는 Webflux라고 해서 ㅂ동기방식의 서버를 지원할때 단일값을 전달한다는 뜻
//                if(config.isPostLogger()){
//                    log.info("Global Filter Start: request id->{}",response.getStatusCode());
//                }
//            }));
//        };
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage: {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Logging PRE Filter Start: request id->{}", request.getId());
            }

            //Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> { //Mono라는 객체는 Webflux라고 해서 ㅂ동기방식의 서버를 지원할때 단일값을 전달한다는 뜻
                if (config.isPostLogger()) {
                    log.info("Logging POST Filter Start: request id->{}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE); //우선순위를 주면 global 보다 더 일찍 필터를 실행되게 할 수 있음

        return filter;
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

}
