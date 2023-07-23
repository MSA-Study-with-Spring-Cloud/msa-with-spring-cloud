package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {

    }

    //login->token->user(with token)->header(includ token)
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            //토큰이 포함된 채로 왔는지
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authrozationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authrozationHeader.replace("Bearer", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "Jwt token is not valid", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        });
    }

    //Mono그냥 단위값. 이외 Flux(여러개의 값을 가질경우)라는 단위값이 있음. Spring WebFlux에서 추가된 방식
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response=exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue=true;
        String subject=null;

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        }catch (Exception e){
            returnValue =false;
        }

        if(subject==null||subject.isEmpty()){
            returnValue=false;
        }
        return returnValue;
    }
}