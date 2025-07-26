package com.itil.config;

import com.itil.filter.JwtAuthenticationFilter;
import com.itil.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter authFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/api/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html", "/api/users/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/users/**", "/api/tickets/{userId}", "/api/users/{id}", "/api/tickets/{ticketId}", "/api/trainings/{trainingId}/feedback", "/api/users/{userId}", "/api/tickets/user/{raisedById}").hasAuthority("ROLE_USER")
                        .requestMatchers("/api/gatekeeper/**", "/api/tickets/status/**", "/api/tickets/priority/{priority}", "/api/tickets/user/{raisedById}",  "/api/tickets/count/category", "/api/tickets/category/{ticketCategory}", "/api/tickets", "/api/trainings/{trainingId}/assignments/{ticketId}", "/api/trainings/{trainingId}", "/api/trainings/{trainingId}/getFeedback", "/api/trainings/certificate/{ticketId}", "/api/trainings/training", "/api/gatekeeper/{teamMemberId}/gatekeeper/{gatekeeperId}").hasAuthority("ROLE_GATEKEEPER")
                        .requestMatchers("/api/teamMembers/{teamMemberId}/tickets", "/api/teamMembers/tickets/{ticketId}/assignees/{teamMemberId}", "/api/teamMembers/{teamMemberId}/tickets/priority/{priorityLevel}", "/api/tickets/update/**", "/api/tickets/status/{ticketStatus}").hasAuthority("ROLE_TEAMMEMBER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

