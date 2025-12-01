package com.example.enviexpress.security;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity // <- “Activa la configuración personalizada de seguridad web que yo voy a definir.”
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login","/css/**", "/js/**", "/img/**").permitAll()  //cualquier usuario sin autenticar
                .requestMatchers("/usuarios/**").hasRole("ADMIN")  // rutas y subrutas, solo permitidas a Perfil ADMIN
                .requestMatchers("/perfil/**").authenticated()    // rutas permitidas para usuarios autenticados : actualiza perfil usuario
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // comportamiento del proceso de inicio de sesión
                 .successHandler(customAuthenticationSuccessHandler()) // aquí usamos nuestro handler   //.defaultSuccessUrl("/home", true) //cuando el usuario se logea, se dirige al home.html
                .permitAll()   //permitido a cualquier usuario
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll() //permitido a cualquier usuario
            );
 

            
        return http.build();
    }


        // Handler para redirigir según el rol
        @Bean
        public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
            return (request, response, authentication) -> {
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                if (roles.contains("ROLE_ADMIN")) {
                    response.sendRedirect("/home"); // página para admin
                } else {
                    response.sendRedirect("/home_cliente");  // página para usuarios normales
                }
            };
        }
 

}
