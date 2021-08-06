package com.github.px.custom.util;


import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import java.text.ParseException;
import java.util.Map;

public class JwtUtils {

    public static Map<String, Object> parseToken(String token) throws ParseException {
        JWT jwt = JWTParser.parse(token);
        JWTClaimsSet jwtClaimsSet =jwt.getJWTClaimsSet();
        return jwtClaimsSet.getClaims();
    }

}
