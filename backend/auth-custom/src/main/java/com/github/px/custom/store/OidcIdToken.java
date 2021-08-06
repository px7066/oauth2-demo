package com.github.px.custom.store;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OidcIdToken implements IdTokenClaimAccessor{
    public static final String TOKEN_RESULT_KEY = "Id_TOKEN_KEY";

    private final Map<String, Object> claims;

    private final Instant issuedAt;

    private final Instant expiresAt;

    private final String tokenValue;

    public OidcIdToken(String tokenValue, Instant issuedAt, Instant expiresAt, Map<String, Object> claims) {
        this.claims = claims;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.tokenValue = tokenValue;
    }

    public static OidcIdToken.Builder withTokenValue(String tokenValue) {
        return new OidcIdToken.Builder(tokenValue);
    }

    @Override
    public String getUsername() {
        if(!ObjectUtils.isEmpty(claims)){
            return (String) claims.get("sub");
        }
        return null;
    }

    public static final class Builder {
        private String tokenValue;
        private final Map<String, Object> claims;

        private Builder(String tokenValue) {
            this.claims = new LinkedHashMap();
            this.tokenValue = tokenValue;
        }

        public OidcIdToken.Builder tokenValue(String tokenValue) {
            this.tokenValue = tokenValue;
            return this;
        }

        public OidcIdToken.Builder claim(String name, Object value) {
            this.claims.put(name, value);
            return this;
        }

        public OidcIdToken.Builder claims(Consumer<Map<String, Object>> claimsConsumer) {
            claimsConsumer.accept(this.claims);
            return this;
        }

        public OidcIdToken.Builder accessTokenHash(String accessTokenHash) {
            return this.claim("at_hash", accessTokenHash);
        }

        public OidcIdToken.Builder audience(Collection<String> audience) {
            return this.claim("aud", audience);
        }

        public OidcIdToken.Builder authTime(Instant authenticatedAt) {
            return this.claim("auth_time", authenticatedAt);
        }

        public OidcIdToken.Builder authenticationContextClass(String authenticationContextClass) {
            return this.claim("acr", authenticationContextClass);
        }

        public OidcIdToken.Builder authenticationMethods(List<String> authenticationMethods) {
            return this.claim("amr", authenticationMethods);
        }

        public OidcIdToken.Builder authorizationCodeHash(String authorizationCodeHash) {
            return this.claim("c_hash", authorizationCodeHash);
        }

        public OidcIdToken.Builder authorizedParty(String authorizedParty) {
            return this.claim("azp", authorizedParty);
        }

        public OidcIdToken.Builder expiresAt(Instant expiresAt) {
            return this.claim("exp", expiresAt);
        }

        public OidcIdToken.Builder issuedAt(Instant issuedAt) {
            return this.claim("iat", issuedAt);
        }

        public OidcIdToken.Builder issuer(String issuer) {
            return this.claim("iss", issuer);
        }

        public OidcIdToken.Builder nonce(String nonce) {
            return this.claim("nonce", nonce);
        }

        public OidcIdToken.Builder subject(String subject) {
            return this.claim("sub", subject);
        }

        public OidcIdToken build() {
            Instant iat = this.toInstant(this.claims.get("iat"));
            Instant exp = this.toInstant(this.claims.get("exp"));
            return new OidcIdToken(this.tokenValue, iat, exp, this.claims);
        }

        private Instant toInstant(Object timestamp) {
            if (timestamp != null) {
                Assert.isInstanceOf(Instant.class, timestamp, "timestamps must be of type Instant");
            }

            return (Instant)timestamp;
        }
    }

    public String getTokenValue() {
        return this.tokenValue;
    }

    @Nullable
    public Instant getIssuedAt() {
        return this.issuedAt;
    }

    @Nullable
    public Instant getExpiresAt() {
        return this.expiresAt;
    }
}
