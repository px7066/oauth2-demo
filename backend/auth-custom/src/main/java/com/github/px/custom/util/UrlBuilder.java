package com.github.px.custom.util;

import javax.servlet.http.HttpServletRequest;

public class UrlBuilder {
    public static String buildRedirectUrl(HttpServletRequest request){
        return buildFullRequestUrl(request.getScheme(), request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString());
    }

    public static String buildFullRequestUrl(String scheme, String serverName, int serverPort, String requestURI, String queryString) {
        scheme = scheme.toLowerCase();
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if ("http".equals(scheme)) {
            if (serverPort != 80) {
                url.append(":").append(serverPort);
            }
        } else if ("https".equals(scheme) && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(requestURI);
        if (queryString != null) {
            url.append("?").append(queryString);
        }

        return url.toString();
    }
}
