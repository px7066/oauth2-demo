package com.github.px.custom.util;

import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StringUtils {
    public static Map<String, String> splitUrlParameters(String url){
        if(!ObjectUtils.isEmpty(url)){
            Map<String, String> params = new HashMap<>();
            Pattern andPattern = Pattern.compile("&");
            String[] andStrs = andPattern.split(url);
            if(andStrs == null){
                return params;
            }
            if(andStrs.length >0){
                Pattern equalPattern = Pattern.compile("=");
                for (String paramsString : andStrs) {
                    if(!ObjectUtils.isEmpty(paramsString)){
                        String[] keyValues = equalPattern.split(paramsString);
                        if(keyValues.length > 1){
                            params.put(keyValues[0], keyValues[1]);
                        }
                    }
                }
            }
            return params;
        }
        return new HashMap<>();
    }
}
