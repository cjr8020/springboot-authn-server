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