package com.github.px.custom.store;

public class IdTokenContext {
    private static final ThreadLocal<OidcIdToken> idToken = new ThreadLocal<>();

    public static void setIdToken(OidcIdToken oidcIdToken){
        idToken.set(oidcIdToken);
    }

    public static OidcIdToken getIdToken(){
        return idToken.get();
    }
}
