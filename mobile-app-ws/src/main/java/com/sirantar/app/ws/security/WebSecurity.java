package com.sirantar.app.ws.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sirantar.app.ws.io.repositories.UserRepository;
import com.sirantar.app.ws.service.UserService;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  private final UserService           userDetailsService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserRepository        userRespository;

  public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,
    UserRepository userRepository) {
    this.userDetailsService    = userDetailsService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userRespository       = userRepository;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and()
      .csrf().disable().authorizeRequests()
      .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
      .permitAll()
      .antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL)
      .permitAll()
      .antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL)
      .permitAll()
      .antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL)
      .permitAll()
      .antMatchers(SecurityConstants.H2_CONSOLE)
      .permitAll()
      .antMatchers("/v2/api-docs", "/v3/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**")
      .permitAll()
      // .antMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
      // .antMatchers(HttpMethod.DELETE, "/users/**").hasAnyRole("ADMIN","SUPER_ADMIN")
      // .antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("DELETE_AUTHORITY")
      //.antMatchers(HttpMethod.DELETE, "/users/**").hasAnyAuthority("DELETE_AUTHORITY", "DELETE_ALL_AUTHORITY")
      .anyRequest().authenticated().and()
      .addFilter(getAuthenticationFilter())
      .addFilter(new AuthorizationFilter(authenticationManager(), userRespository))
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.headers().frameOptions().disable(); // avoid that frame are disallow in browser, only for testing H2 with
                                            // frontend
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

  public AuthenticationFilter getAuthenticationFilter() throws Exception {
    final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
    filter.setFilterProcessesUrl("/users/login");
    return filter;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-type"));

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
