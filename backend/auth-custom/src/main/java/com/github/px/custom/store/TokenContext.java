package com.github.px.custom.store;

public class TokenContext {
    private static final ThreadLocal<TokenResult> TOKEN_RESULT_THREAD_LOCAL = new ThreadLocal<>();

    public static void setTokenResult(TokenResult tokenResult){
        TOKEN_RESULT_THREAD_LOCAL.set(tokenResult);
    }

    public static TokenResult getTokenResult(){
        return TOKEN_RESULT_THREAD_LOCAL.get();
    }
}
