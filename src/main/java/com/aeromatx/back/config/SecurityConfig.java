package com.aeromatx.back.config;

import com.aeromatx.back.security.JwtAuthenticationEntryPoint;
import com.aeromatx.back.security.JwtAuthenticationFilter;
import com.aeromatx.back.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ✅ Global CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://127.0.0.1:5500", // Live Server
            "http://localhost:5500", // Direct browser run
            "http://localhost:3000", // React
            "http://localhost:4200", // Angular
            "https://ecommercebackend-i16e.onrender.com" // Replace with prod URL
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Allow cookies/JWT

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors() // ✅ Enable CORS
            .and()
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html", "/*.html", "/*.css", "/*.js",
                    "/css/**", "/css2/**", "/css3/**",
                    "/js/**", "/js2/**",
                    "/img/**", "/fonts/**", "/lib/**",
                    "/favicon.ico", "/error", "/assets/**", "/uploads/**",
                    "/admin_pannel/**",
                    "/header.html", "/footer.html",
                    "/api/subcategories/by-category/**"
                ).permitAll()

                .requestMatchers(HttpMethod.POST, "/api/vendor/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/vendor", "/api/vendor/{id}").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/vendor/{id}").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/vendor/{id}").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/users/by-role").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()
                .requestMatchers("/admin_pannel/admin-login.html").permitAll()
                .requestMatchers("/admin_pannel/customers.html").permitAll()
                .requestMatchers("/Vender-pannel/dashboard.html").permitAll()

                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
