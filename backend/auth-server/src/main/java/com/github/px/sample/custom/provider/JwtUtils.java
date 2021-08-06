package com.github.px.sample.custom.provider;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JoseHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JoseHeader.Builder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

final class JwtUtils {
    private JwtUtils() {
    }

    static Builder headers() {
        return JoseHeader.withAlgorithm(SignatureAlgorithm.RS256);
    }

    static org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder accessTokenClaims(RegisteredClient registeredClient, String issuer, String subject, Set<String> authorizedScopes) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(registeredClient.getTokenSettings().accessTokenTimeToLive());
        org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }

        claimsBuilder.subject(subject).audience(Collections.singletonList(registeredClient.getClientId())).issuedAt(issuedAt).expiresAt(expiresAt).notBefore(issuedAt);
        if (!CollectionUtils.isEmpty(authorizedScopes)) {
            claimsBuilder.claim("scope", authorizedScopes);
        }

        return claimsBuilder;
    }

    static org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder idTokenClaims(RegisteredClient registeredClient, String issuer, String subject, String nonce) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30L, ChronoUnit.MINUTES);
        org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }

        claimsBuilder.subject(subject).audience(Collections.singletonList(registeredClient.getClientId())).issuedAt(issuedAt).expiresAt(expiresAt).claim("azp", registeredClient.getClientId());
        if (StringUtils.hasText(nonce)) {
            claimsBuilder.claim("nonce", nonce);
        }

        return claimsBuilder;
    }
}