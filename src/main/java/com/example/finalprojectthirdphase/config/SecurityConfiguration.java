package com.example.finalprojectthirdphase.config;

import com.example.finalprojectthirdphase.service.AdminService;
import com.example.finalprojectthirdphase.service.CustomerService;
import com.example.finalprojectthirdphase.service.ExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomerService customerService;
    private final ExpertService expertService;
    private final AdminService adminService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        s -> s.anyRequest().permitAll()).httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Autowired
    public void configureCustomerBuild(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(username -> customerService
                        .findByUsernameOP(username)
                        .orElseThrow(() -> new UsernameNotFoundException("not found")))
                .passwordEncoder(passwordEncoder);
    }

    @Autowired
    public void configureExpertBuild (AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(username -> expertService
                        .findByUsernameOP(username)
                        .orElseThrow(() -> new UsernameNotFoundException("not found")))
                .passwordEncoder(passwordEncoder);
    }

    @Autowired
    public void configureAdminBuild (AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(username -> adminService
                        .findByUsernameOP(username)
                        .orElseThrow(() -> new UsernameNotFoundException("not found")))
                .passwordEncoder(passwordEncoder);
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

