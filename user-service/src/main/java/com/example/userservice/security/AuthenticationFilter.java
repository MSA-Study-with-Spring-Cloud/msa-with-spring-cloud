package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private Environment env;


    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        //로그인시 세번째로 수행되는 과정으로 이 함수가 사용자가 로그인을 하게되면 제일 먼저 시도되는 함수
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
            //inputstream으로 받은 이유는 전달 시켜주려고 하는 로그인의 값은 POST 형태인데 그럼 requestParameter을 받을 수 없기 때문에,
            // inputStream으로 받으면 수작업으로 어떤 데이터가 들어와쓴ㄴ지를 처리할 수 있음.

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
            // 사용자가 입려한 아이디와 메일 등 입력값을 spring security에서 사용하기 위한 형태로 바꿔주기 위해
            // UsernamePasswordAuthenticationToken값으로 변화시켜붐
            // 이걸 AuthenticationManager의 authenticate에 넘기면 아이디와 패스워드를 비교하는 인증처리를 해주겠다
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        //로그인시 다섯번째로 수행

        String userName= ((User)authResult.getPrincipal()).getUsername();
        UserDto userDto=userService.getUserDetailsByEmail(userName);
    }
}
