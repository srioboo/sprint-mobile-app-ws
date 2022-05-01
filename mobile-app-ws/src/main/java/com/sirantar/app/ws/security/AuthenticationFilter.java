package com.sirantar.app.ws.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirantar.app.ws.SpringApplicationContext;
import com.sirantar.app.ws.service.UserService;
import com.sirantar.app.ws.shared.dto.UserDto;
import com.sirantar.app.ws.ui.model.request.UserLoginRequestModel;

import io.jsonwebtoken.Jwts;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public AuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
    throws AuthenticationException {

    try {
      UserLoginRequestModel creds = new ObjectMapper()
        .readValue(req.getInputStream(), UserLoginRequestModel.class);

      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                                                                        creds.getEmail(),
                                                                                        creds.getPassword(),
                                                                                        new ArrayList<>()));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
    HttpServletResponse res,
    FilterChain chain,
    Authentication auth) throws IOException, ServletException {

    String userName = ((UserPrincipal) auth.getPrincipal()).getUsername();

    String token = Jwts.builder()
      .setSubject(userName)
      .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
      .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
      .compact();

    UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl"); // el nombre del bean tiene que empezar con minusculas

    UserDto userDto = userService.getUser(userName);

    res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    res.addHeader("UserID", userDto.getUserId());

    // added expose to be able to get bearer in the front-end
    res.addHeader("Access-Control-Expose-Headers", "Authorization, X-Custom-Header");
  }
}
