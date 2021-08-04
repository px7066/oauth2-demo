/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.px.sample.config;

import com.github.px.sample.config.configurer.AuthServerConfigurer;
import com.github.px.sample.custom.CustomAuthenticationFailureHandler;
import com.github.px.sample.custom.CustomLoginUrlAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * <p>Security config </p>
 *
 * @author panxi
 * @version 1.0.0
 * @date 2021/8/3
 */
@Configuration
public class DefaultSecurityConfig {

	@Autowired
	private UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource;

	@Autowired
	private AuthServerConfigurer authServerConfigurer;

	// @formatter:off
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		AuthenticationEntryPoint authenticationEntryPoint = new CustomLoginUrlAuthenticationEntryPoint(authServerConfigurer.getLoginFormUrl());
		// 认证失败处理
		AuthenticationFailureHandler authenticationFailureHandler = new CustomAuthenticationFailureHandler(authServerConfigurer.getFailureUrl());

		http
			.authorizeRequests().antMatchers("/login", "/loginPage","/getCsrfToken", "/index.html", "/favicon.ico","/","/js/**", "/css/**")
				.permitAll()
			.and()
			.authorizeRequests(authorizeRequests ->
				authorizeRequests.anyRequest().authenticated()
			)
			.formLogin()
				.loginPage(authServerConfigurer.getLoginFormUrl())
				.loginProcessingUrl("/login")
				.failureHandler(authenticationFailureHandler)
			.and()
			.cors()
				.configurationSource(urlBasedCorsConfigurationSource)
			.and()
			.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
			.and()
			.csrf()
				.disable();
		return http.build();
	}
}
