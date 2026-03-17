// package com.billpoint.backend.security;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// @EnableMethodSecurity
// public class WebSecurityConfig {
//     @Autowired
//     UserDetailsServiceImpl userDetailsService;

//     @Autowired
//     private AuthEntryPointJwt unauthorizedHandler;

//     @Bean
//     public AuthTokenFilter authenticationJwtTokenFilter() {
//         return new AuthTokenFilter();
//     }

//     @Bean
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
//         authProvider.setUserDetailsService(userDetailsService);
//         authProvider.setPasswordEncoder(passwordEncoder());
   
//         return authProvider;
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//         return authConfig.getAuthenticationManager();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     // @Bean
//     // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//     //     http.csrf(AbstractHttpConfigurer::disable)
//     //         .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
//     //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//     //         .authorizeHttpRequests(auth -> 
//     //             auth.requestMatchers("/api/auth/**").permitAll()
//     //                 .requestMatchers("/api/test/**").permitAll()
//     //                 .anyRequest().authenticated()
//     //         );
        
//     //     http.authenticationProvider(authenticationProvider());
//     //     http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
//     //     return http.build();
//     // }
// // @Bean
// // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

// //     http
// //         .csrf(AbstractHttpConfigurer::disable)
// //         .cors(cors -> {})
// //         .sessionManagement(session ->
// //                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
// //         .authorizeHttpRequests(auth -> auth
// //                 .requestMatchers("/api/auth/**").permitAll()
// //                 .requestMatchers("/error").permitAll()
// //                 .anyRequest().authenticated()
// //         );

// //     http.authenticationProvider(authenticationProvider());

// //     http.addFilterBefore(authenticationJwtTokenFilter(),
// //             UsernamePasswordAuthenticationFilter.class);

// //     return http.build();
// // }

// @Bean
// public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//     http
//         .csrf(AbstractHttpConfigurer::disable)
//         .cors(cors -> {})
//         .sessionManagement(session ->
//                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//         .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/api/auth/signin").permitAll()
//                 .requestMatchers("/api/auth/signup").permitAll()
//                 .requestMatchers("/error").permitAll()
//                 .anyRequest().authenticated()
//         );

//     http.authenticationProvider(authenticationProvider());

//     http.addFilterBefore(authenticationJwtTokenFilter(),
//             UsernamePasswordAuthenticationFilter.class);

//     return http.build();
// }
// }


package com.billpoint.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // ✅ JWT Filter
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // ✅ Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // ✅ Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ✅ Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ MAIN SECURITY CONFIG
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})

            // 🔥 Handle unauthorized properly
            .exceptionHandling(exception ->
                    exception.authenticationEntryPoint(unauthorizedHandler))

            // 🔥 Stateless (JWT)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 🔥 Authorization rules
            .authorizeHttpRequests(auth -> auth

                    // ✅ Public APIs
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/error").permitAll()

                    // 🔥 ADMIN APIs (FIXED)
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")

                    // ✅ Everything else requires login
                    .anyRequest().authenticated()
            );

        // ✅ Attach authentication provider
        http.authenticationProvider(authenticationProvider());

        // ✅ Add JWT filter
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}