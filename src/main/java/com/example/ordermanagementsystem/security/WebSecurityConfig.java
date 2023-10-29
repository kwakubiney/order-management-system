package com.example.ordermanagementsystem.security;

import com.example.ordermanagementsystem.security.jwt.JWTAuthorizationFilter;
import com.example.ordermanagementsystem.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    protected JwtUtils tokenUtils;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    protected PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }


    @Bean
    protected AuthenticationManager getAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Required to make H2-console work
                .headers().frameOptions().disable()
                .and()
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/graphql").permitAll()
                .antMatchers("/graphiql", "/vendor/graphiql/*").permitAll()
                .antMatchers("/h2-console/*").permitAll()
                .anyRequest().denyAll()
                .and()
                .addFilter(new JWTAuthorizationFilter(getAuthenticationManager(), tokenUtils))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.cors();
    }
}