package com.example.userservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //users로 들어오는 경로에 대해서는 인증작업 없이 사용 가능
        //http.authorizeHttpRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("192.168.35.151")
                .and()
                .addFilter(getAuthenticaionFilter());
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticaionFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

}
