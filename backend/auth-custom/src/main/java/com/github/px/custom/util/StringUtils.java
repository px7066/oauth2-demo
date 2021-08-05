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
            String[] paramsStrings = new String[andStrs.length];
            if(andStrs.length >0){
                System.arraycopy(andStrs, 1, paramsStrings, 1, andStrs.length -1);
                Pattern questionPattern = Pattern.compile("\\?");
                String[] questionStrings = questionPattern.split(andStrs[0]);
                if(questionStrings.length > 1){
                    System.arraycopy(questionStrings, 1, paramsStrings, 0, questionStrings.length -1);
                }
                Pattern equalPattern = Pattern.compile("=");
                for (String paramsString : paramsStrings) {
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
