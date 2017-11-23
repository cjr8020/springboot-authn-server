package com.standard.demo.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  private static final Logger log = LoggerFactory.getLogger(AuthorizationServerConfiguration.class);

  @Value("${spring.security.jwt.client-id}")
  private String clientId;

//  @Value("${spring.security.jwt.client-secret}")
//  private String clientSecret;

  /* using autowired ConfigurationProperties b/c this is a list */
  @Autowired
  private GrantTypesConfig grantTypes;

  /* using autowired ConfigurationProperties b/c this is a list */
  @Autowired
  private ScopesConfig scopes;

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

//    log.debug("clientId: {}", clientId);
//    log.debug("grantTypes: {}", grantTypes.getGrantTypes());
//    log.debug("scopes: {}", scopes.getScopes());
//    log.debug("resourceIds: {}", resourceIds);

    configurer
        .inMemory()
        .withClient(this.clientId)
//        .secret(this.clientSecret)
        .authorizedGrantTypes(this.grantTypes.getGrantTypes().stream().toArray(String[]::new))
        .scopes(this.scopes.getScopes().stream().toArray(String[]::new))
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

@Configuration
@ConfigurationProperties(prefix="spring.security.jwt")
class GrantTypesConfig {
  private List<String> grantTypes = new ArrayList<>();
  public List<String> getGrantTypes() { return this.grantTypes; }
}

@Configuration
@ConfigurationProperties(prefix="spring.security.jwt")
class ScopesConfig {
  private List<String> scopes = new ArrayList<>();
  public List<String> getScopes() { return this.scopes; }
}
