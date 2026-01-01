package com.apexcart.order.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes!=null){
                HttpServletRequest request = attributes.getRequest();
                String usernameHeader = request.getHeader("loggedInUser");
                if (usernameHeader != null) {
                    requestTemplate.header("loggedInUser", request.getHeader("loggedInUser"));
                    requestTemplate.header("loggedInUserRoles", request.getHeader("loggedInUserRoles"));                }
            }
        };
    }
}
