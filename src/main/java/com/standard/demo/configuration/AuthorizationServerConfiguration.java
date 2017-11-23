package com.standard.demo.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  @Value("${spring.security.jwt.client-id}")
  private String clientId;

  @Value("${spring.security.jwt.client-secret}")
  private String clientSecret;

//  @Value("${spring.security.jwt.grant-type}")
//  private String grantType;

  @Value("${spring.security.jwt.grant-types}")
  private List<String> grantTypes = new ArrayList<>();
  public List<String> getGrantTypes() { return this.grantTypes; }

//  @Value("${spring.security.jwt.scope-read}")
//  private String scopeRead;
//
//  @Value("${spring.security.jwt.scope-write}")
//  private String scopeWrite = "write";

  @Value("${spring.security.jwt.scopes}")
  private List<String> scopes = new ArrayList<>();
  public List<String> getScopes() { return this.scopes; }

  @Value("${spring.security.jwt.resource-ids}")
  private String resourceIds;

  @Value("${spring.security.jwt.access-token-validity}")
  private int accessTokenValidity;

  @Value("${spring.security.jwt.refresh-token-validity}")
  private int refreshTokenValidity;


  @Autowired
  private TokenStore tokenStore;

  @Autowired
  private JwtAccessTokenConverter accessTokenConverter;

  @Autowired
  private AuthenticationManager authenticationManager;


  /**
   * This configurer enables 'untrusted' client token requests.
   * 'trusted' clients are required to use secret with Basic Auth in order to access /oauth/token
   * endpoint.
   * web client (e.g. user agent) are inherently 'untrusted'. This configuration enables the client
   * to user form parameters, instead of basic auth.
   * @param security
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) {
    security
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("isAuthenticated()")
        .allowFormAuthenticationForClients();
  }

  /**
   * This configurer defines the client details service, and is a callback from
   * AuthorizationServerConfigurer.
   *
   * The OAuth2.0 client is an application that wants to access resources on behalf of a user.
   * This client can be an external web application, a user agent, or a native client.
   *
   * @param configurer
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
    configurer
        .inMemory()
        .withClient(this.clientId)
        .secret(this.clientSecret)
        .authorizedGrantTypes((String[])this.grantTypes.toArray())
//        .scopes(this.scopeRead, this.scopeWrite)
        .scopes((String[])this.scopes.toArray())
        .resourceIds(this.resourceIds)
        .accessTokenValiditySeconds(this.accessTokenValidity)
        .refreshTokenValiditySeconds(this.refreshTokenValidity)
    ;
  }

  /**
   * Defines the authorization and token endpoints
   * @param endpoints
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
    enhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
    endpoints.tokenStore(tokenStore)
        .accessTokenConverter(accessTokenConverter)
        .tokenEnhancer(enhancerChain)
        .authenticationManager(authenticationManager);
  }
}
