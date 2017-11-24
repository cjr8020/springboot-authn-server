# Spring Boot OAuth2 Authn Server Demo



## Access Token Request

[source](https://tools.ietf.org/html/rfc6749)

The client makes a request to the token endpoint by adding the
   following parameters using the "application/x-www-form-urlencoded"
   format per Appendix B with a character encoding of UTF-8 in the HTTP
   request entity-body:

```
   grant_type
         REQUIRED.  Value MUST be set to "password".

   username
         REQUIRED.  The resource owner username.

   password
         REQUIRED.  The resource owner password.

   scope
         OPTIONAL.  The scope of the access request as described by
         Section 3.3.
```

## Handling 'untrusted' web client

`.secret` is removed from authn server config,


```
    configurer
        .inMemory()
        .withClient(this.clientId)
//        .secret(this.clientSecret)
```


therefore `clientId` is the only required attribute, and it is passed like this:

`test-web-clientid:@localhost:8080/oauth/token`

in other words, the `Authorization` header is still present, however
the password is not provided.


## access token


```
$ curl -X POST test-web-clientid:@localhost:8080/oauth/token -d grant_type=password -d username=john.doe -d password=jwtpass
```

response:

```
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidGVzdC1vYXV0aDItcmVzb3VyY2VpZCJdLCJ1c2VyX25hbWUiOiJqb2huLmRvZSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSIsInRydXN0Il0sImV4cCI6MTUxMTUyMjQyMiwiYXV0aG9yaXRpZXMiOlsiU1RBTkRBUkRfVVNFUiJdLCJqdGkiOiJkMDU0ODI0NC1iY2U3LTRjYzgtYjVjYS1iNWIwNTlmN2UzNWYiLCJjbGllbnRfaWQiOiJ0ZXN0LXdlYi1jbGllbnRpZCJ9.vJBxvQZvZJJ1SGF0D6jKpZSSlS_ZntygjH7ZhqAoMW0",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidGVzdC1vYXV0aDItcmVzb3VyY2VpZCJdLCJ1c2VyX25hbWUiOiJqb2huLmRvZSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSIsInRydXN0Il0sImF0aSI6ImQwNTQ4MjQ0LWJjZTctNGNjOC1iNWNhLWI1YjA1OWY3ZTM1ZiIsImV4cCI6MTUxNDA3MTIyMiwiYXV0aG9yaXRpZXMiOlsiU1RBTkRBUkRfVVNFUiJdLCJqdGkiOiJhOTZlYjY5ZS03Nzc2LTRjZTItODI5Mi01YTczYmNiMDYzMjIiLCJjbGllbnRfaWQiOiJ0ZXN0LXdlYi1jbGllbnRpZCJ9.wyWU4Cf4BCDM6pMSB821278R-7minXHiyEhHbE_zkSY",
    "expires_in": 43199,
    "scope": "read write trust",
    "jti": "d0548244-bce7-4cc8-b5ca-b5b059f7e35f"
}
```

## TODO: refresh token

https://github.com/spring-projects/spring-security-oauth/issues/813

https://stackoverflow.com/questions/43842659/refresh-token-call-fails-using-spring-security-an-oauth2-with-error-userdetails

error:

```
2017-11-23 15:42:38.175 DEBUG 18971 --- [nio-8080-exec-3] o.s.s.o.p.refresh.RefreshTokenGranter    : Getting access token for: test-web-clientid
2017-11-23 15:42:38.199 DEBUG 18971 --- [nio-8080-exec-3] o.s.s.authentication.ProviderManager     : Authentication attempt using org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
2017-11-23 15:42:38.199 DEBUG 18971 --- [nio-8080-exec-3] p.PreAuthenticatedAuthenticationProvider : PreAuthenticated authentication request: org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken@aaade717: Principal: org.springframework.security.authentication.UsernamePasswordAuthenticationToken@11ee24de: Principal: john.doe; Credentials: [PROTECTED]; Authenticated: true; Details: null; Granted Authorities: STANDARD_USER; Credentials: [PROTECTED]; Authenticated: true; Details: null; Granted Authorities: STANDARD_USER
2017-11-23 15:42:38.200 DEBUG 18971 --- [nio-8080-exec-3] .m.m.a.ExceptionHandlerExceptionResolver : Resolving exception from handler [public org.springframework.http.ResponseEntity<org.springframework.security.oauth2.common.OAuth2AccessToken> org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(java.security.Principal,java.util.Map<java.lang.String, java.lang.String>) throws org.springframework.web.HttpRequestMethodNotSupportedException]: java.lang.IllegalStateException: UserDetailsService is required.
2017-11-23 15:42:38.202 DEBUG 18971 --- [nio-8080-exec-3] .m.m.a.ExceptionHandlerExceptionResolver : Invoking @ExceptionHandler method: public org.springframework.http.ResponseEntity<org.springframework.security.oauth2.common.exceptions.OAuth2Exception> org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.handleException(java.lang.Exception) throws java.lang.Exception
2017-11-23 15:42:38.202  INFO 18971 --- [nio-8080-exec-3] o.s.s.o.provider.endpoint.TokenEndpoint  : Handling error: IllegalStateException, UserDetailsService is required.
2017-11-23 15:42:38.224 DEBUG 18971 --- [nio-8080-exec-3] o.s.w.s.m.m.a.HttpEntityMethodProcessor  : Written [error="server_error", error_description="UserDetailsService is required."] as "application/json" using [org.springframework.http.converter.json.MappingJackson2HttpMessageConverter@46476bc3]
```

