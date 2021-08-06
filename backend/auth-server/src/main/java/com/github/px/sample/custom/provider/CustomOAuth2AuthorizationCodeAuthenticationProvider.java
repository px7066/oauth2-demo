package com.github.px.sample.custom.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.jwt.JoseHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CustomOAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {

    private static final StringKeyGenerator TOKEN_GENERATOR = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);
    private static final OAuth2TokenType AUTHORIZATION_CODE_TOKEN_TYPE = new OAuth2TokenType("code");
    private static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE = new OAuth2TokenType("id_token");
    private final OAuth2AuthorizationService authorizationService;
    private final JwtEncoder jwtEncoder;
    private OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = (context) -> {
    };
    private ProviderSettings providerSettings;

    public CustomOAuth2AuthorizationCodeAuthenticationProvider(OAuth2AuthorizationService authorizationService,JwtEncoder jwtEncoder) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(jwtEncoder, "jwtEncoder cannot be null");
        this.authorizationService = authorizationService;
        this.jwtEncoder = jwtEncoder;
    }

    public final void setJwtCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        Assert.notNull(jwtCustomizer, "jwtCustomizer cannot be null");
        this.jwtCustomizer = jwtCustomizer;
    }

    @Autowired(
            required = false
    )
    protected void setProviderSettings(ProviderSettings providerSettings) {
        this.providerSettings = providerSettings;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2AuthorizationCodeAuthenticationToken authorizationCodeAuthentication = (OAuth2AuthorizationCodeAuthenticationToken)authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient(authorizationCodeAuthentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        OAuth2Authorization authorization = this.authorizationService.findByToken(authorizationCodeAuthentication.getCode(), AUTHORIZATION_CODE_TOKEN_TYPE);
        if (authorization == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_grant"));
        } else {
            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
            OAuth2AuthorizationRequest authorizationRequest = (OAuth2AuthorizationRequest)authorization.getAttribute(OAuth2AuthorizationRequest.class.getName());
            if (!registeredClient.getClientId().equals(authorizationRequest.getClientId())) {
                if (!authorizationCode.isInvalidated()) {
                    authorization = OAuth2AuthenticationProviderUtils.invalidate(authorization, authorizationCode.getToken());
                    this.authorizationService.save(authorization);
                }

                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_grant"));
            } else if (StringUtils.hasText(authorizationRequest.getRedirectUri()) && !authorizationRequest.getRedirectUri().equals(authorizationCodeAuthentication.getRedirectUri())) {
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_grant"));
            } else if (!authorizationCode.isActive()) {
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_grant"));
            } else {
                String issuer = this.providerSettings != null ? this.providerSettings.issuer() : null;
                Set<String> authorizedScopes = (Set)authorization.getAttribute(OAuth2Authorization.AUTHORIZED_SCOPE_ATTRIBUTE_NAME);
                JoseHeader.Builder headersBuilder = JwtUtils.headers();
                JwtClaimsSet.Builder claimsBuilder = JwtUtils.accessTokenClaims(registeredClient, issuer, authorization.getPrincipalName(), authorizedScopes);
                JwtEncodingContext context = ((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)JwtEncodingContext.with(headersBuilder, claimsBuilder).registeredClient(registeredClient)).principal((Authentication)authorization.getAttribute(Principal.class.getName()))).authorization(authorization)).authorizedScopes(authorizedScopes)).tokenType(OAuth2TokenType.ACCESS_TOKEN)).authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)).authorizationGrant(authorizationCodeAuthentication)).build();
                this.jwtCustomizer.customize(context);
                JoseHeader headers = context.getHeaders().build();
                JwtClaimsSet claims = context.getClaims().build();
                Jwt jwtAccessToken = this.jwtEncoder.encode(headers, claims);
                OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, jwtAccessToken.getTokenValue(), jwtAccessToken.getIssuedAt(), jwtAccessToken.getExpiresAt(), authorizedScopes);
                OAuth2RefreshToken refreshToken = null;
                if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
                    refreshToken = generateRefreshToken(registeredClient.getTokenSettings().refreshTokenTimeToLive());
                }

                Jwt jwtIdToken = null;
                if (authorizationRequest.getScopes().contains("openid")) {
                    String nonce = (String)authorizationRequest.getAdditionalParameters().get("nonce");
                    headersBuilder = JwtUtils.headers();
                    claimsBuilder = JwtUtils.idTokenClaims(registeredClient, issuer, authorization.getPrincipalName(), nonce);
                    context = ((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)((JwtEncodingContext.Builder)JwtEncodingContext.with(headersBuilder, claimsBuilder).registeredClient(registeredClient)).principal((Authentication)authorization.getAttribute(Principal.class.getName()))).authorization(authorization)).authorizedScopes(authorizedScopes)).tokenType(ID_TOKEN_TOKEN_TYPE)).authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)).authorizationGrant(authorizationCodeAuthentication)).build();
                    this.jwtCustomizer.customize(context);
                    headers = context.getHeaders().build();
                    claims = context.getClaims().build();
                    jwtIdToken = this.jwtEncoder.encode(headers, claims);
                }

                OidcIdToken idToken;
                if (jwtIdToken != null) {
                    idToken = new OidcIdToken(jwtIdToken.getTokenValue(), jwtIdToken.getIssuedAt(), jwtIdToken.getExpiresAt(), jwtIdToken.getClaims());
                } else {
                    idToken = null;
                }

                OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization).token(accessToken, (metadata) -> {
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, jwtAccessToken.getClaims());
                });
                if (refreshToken != null) {
                    authorizationBuilder.refreshToken(refreshToken);
                }

                if (idToken != null) {
                    authorizationBuilder.token(idToken, (metadata) -> {
                        metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims());
                    });
                }

                authorization = authorizationBuilder.build();
                authorization = OAuth2AuthenticationProviderUtils.invalidate(authorization, authorizationCode.getToken());
                this.authorizationService.save(authorization);
                Map<String, Object> additionalParameters = Collections.emptyMap();
                if (idToken != null) {
                    additionalParameters = new HashMap();
                    ((Map)additionalParameters).put("id_token", idToken.getTokenValue());
                }

                return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, (Map)additionalParameters);
            }
        }
    }

    public boolean supports(Class<?> authentication) {
        return OAuth2AuthorizationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    static OAuth2RefreshToken generateRefreshToken(Duration tokenTimeToLive) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenTimeToLive);
        return new OAuth2RefreshToken2(TOKEN_GENERATOR.generateKey(), issuedAt, expiresAt);
    }
}
