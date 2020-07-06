
package com.cepengagementservice.security;

//Exposes all the URLs related to JWT Authentication. @author Nicholas Larkin

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins={ "http://localhost:3000", "http://localhost:4200", "http://localhost:8080" }) //CORS will be changed to EC2 servers
public class AuthenticationController {

  @Value("${jwt.http.request.header}") //grabs header from src/main/resources/app.properties 
  private String tokenHeader;

  @Autowired
  private AuthenticationManager authenticationManager; //for UsernamePasswordAuthenticationToken

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private JwtInMemoryUserDetailsService jwtInMemoryUserDetailsService;

  @RequestMapping(value = "${jwt.get.token.uri}", method = RequestMethod.POST)  //Gets Login URI from app.properties and Generates Token if valid
  public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtTokenRequest authenticationRequest)
      throws AuthenticationException { // in case the request is not valid

    authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword()); //authenticates user based on requestbody

    final JwtUserDetails jwtUserDetails = jwtInMemoryUserDetailsService.loadUserByEmail(authenticationRequest.getEmail()); //loads user object

    final String token = jwtTokenUtil.generateToken(jwtUserDetails); //generates token including user object 

    return ResponseEntity.ok(new JwtTokenResponse(token));
  }

  @RequestMapping(value = "${jwt.refresh.token.uri}", method = RequestMethod.GET)  //Gets refresh URI from app.properties and refreshes token if it is expired
  public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
    String authToken = request.getHeader(tokenHeader);
    final String token = authToken.substring(7);
    String email = jwtTokenUtil.getEmailFromToken(token);
    JwtUserDetails user = (JwtUserDetails) jwtInMemoryUserDetailsService.loadUserByEmail(email); //user is not used, possibly trying to default to loadUserbyUsername. Maybe need to use it when I store in H2(?)

    if (jwtTokenUtil.canTokenBeRefreshed(token)) {
      String refreshedToken = jwtTokenUtil.refreshToken(token);
      return ResponseEntity.ok(new JwtTokenResponse(refreshedToken));
    } else {
      return ResponseEntity.badRequest().body(null);
    }
  }

  @ExceptionHandler({ AuthenticationException.class })
  public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  private void authenticate(String email, String password) {
    Objects.requireNonNull(email);
    Objects.requireNonNull(password);

    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)); //still need to override authenticationManager to make it EmailPassword
    } catch (DisabledException e) {
      throw new AuthenticationException("USER_DISABLED", e); //will return error and a message correlated to why.
    } catch (BadCredentialsException e) {
      throw new AuthenticationException("INVALID_CREDENTIALS", e);
    }
  }
}

