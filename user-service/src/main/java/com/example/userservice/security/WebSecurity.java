package com.example.userservice.security;

import com.example.userservice.UserServiceApplication;
import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    //로그인시 두번째로 여기 관련딘 클래스들이 메모리에 올라감
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env,UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.env=env;
        this.userService=userService;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //users로 들어오는 경로에 대해서는 인증작업 없이 사용 가능
        //http.authorizeHttpRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/error/**").permitAll()
                .antMatchers("/**")
                .access("hasIpAddress(\"127.0.0.1\") or hasIpAddress(\"192.168.35.151\") " +
                        "or hasIpAddress(\"172.29.0.1\") or hasIpAddress(\"172.25.32.1\") ")
                .and()
                .addFilter(getAuthenticaionFilter());

        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticaionFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(),userService,env);
        //위에서 생성자를 통해 만들었으니까 아래 authenticationManager는 따로 호출할 필요 없어서 주석
//        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    //select pwd from users where emil=?
    //db_pwd(encrypted)== input_pwd(encrypted)
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
