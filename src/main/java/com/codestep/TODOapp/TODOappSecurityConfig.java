package com.codestep.TODOapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class TODOappSecurityConfig {
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       
       http.csrf(AbstractHttpConfigurer::disable);
       http.headers(headers -> headers
               .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // H2Consoleで必要
       http.authorizeHttpRequests(requests -> requests
               .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
               .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**")).permitAll()
               .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**")).permitAll()
               .requestMatchers(AntPathRequestMatcher.antMatcher("/img/**")).permitAll()
               .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
               .anyRequest().authenticated());
       http.formLogin(form -> form
               .defaultSuccessUrl("/secret"));
       http.logout(LogoutConfigurer::permitAll);

       return http.build();
   }

   @Bean
   public UserDetailsService userDetailsService() {
       String username = "user";
       String password = "pass";

       UserDetails user = User.withUsername(username)
               .password(
                       PasswordEncoderFactories
                               .createDelegatingPasswordEncoder()
                               .encode(password))
               .roles("USER")
               .build();

       return new InMemoryUserDetailsManager(user);
   }
}