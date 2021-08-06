package com.github.px.sample.custom.provider;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwsEncoder;
import org.springframework.util.StringUtils;

import java.util.Map;

public class OAuth2ConfigurerUtils {
    private OAuth2ConfigurerUtils() {
    }

    public static <B extends HttpSecurityBuilder<B>> JwtEncoder getJwtEncoder(B builder) {
        JwtEncoder jwtEncoder = (JwtEncoder)builder.getSharedObject(JwtEncoder.class);
        if (jwtEncoder == null) {
            jwtEncoder = (JwtEncoder)getOptionalBean(builder, JwtEncoder.class);
            if (jwtEncoder == null) {
                JWKSource<SecurityContext> jwkSource = getJwkSource(builder);
                jwtEncoder = new NimbusJwsEncoder(jwkSource);
            }

            builder.setSharedObject(JwtEncoder.class, jwtEncoder);
        }

        return (JwtEncoder)jwtEncoder;
    }



    static <B extends HttpSecurityBuilder<B>> JWKSource<SecurityContext> getJwkSource(B builder) {
        JWKSource<SecurityContext> jwkSource = (JWKSource)builder.getSharedObject(JWKSource.class);
        if (jwkSource == null) {
            ResolvableType type = ResolvableType.forClassWithGenerics(JWKSource.class, new Class[]{SecurityContext.class});
            jwkSource = (JWKSource)getBean(builder, type);
            builder.setSharedObject(JWKSource.class, jwkSource);
        }

        return jwkSource;
    }


    static <B extends HttpSecurityBuilder<B>, T> T getBean(B builder, Class<T> type) {
        return ((ApplicationContext)builder.getSharedObject(ApplicationContext.class)).getBean(type);
    }

    static <B extends HttpSecurityBuilder<B>, T> T getBean(B builder, ResolvableType type) {
        ApplicationContext context = (ApplicationContext)builder.getSharedObject(ApplicationContext.class);
        String[] names = context.getBeanNamesForType(type);
        if (names.length == 1) {
            return (T) context.getBean(names[0]);
        } else if (names.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, names);
        } else {
            throw new NoSuchBeanDefinitionException(type);
        }
    }

    static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, Class<T> type) {
        Map<String, T> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)builder.getSharedObject(ApplicationContext.class), type);
        if (beansMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(type, beansMap.size(), "Expected single matching bean of type '" + type.getName() + "' but found " + beansMap.size() + ": " + StringUtils.collectionToCommaDelimitedString(beansMap.keySet()));
        } else {
            return !beansMap.isEmpty() ? beansMap.values().iterator().next() : null;
        }
    }
}