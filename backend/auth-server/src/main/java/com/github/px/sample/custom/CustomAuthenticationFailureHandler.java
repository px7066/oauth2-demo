package com.github.px.sample.custom;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * <p>认证失败处理</p>
 *
 * @author panxi
 * @version 1.0.0
 * @date 2021/8/3
 */
@Setter
@Getter
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private boolean forwardToDestination = false;
    private boolean allowSessionCreation = true;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String defaultFailureUrl;

    public CustomAuthenticationFailureHandler(String defaultFailureUrl) {
        this.defaultFailureUrl = defaultFailureUrl;
    }



    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        if(this.logger.isTraceEnabled()){
            this.logger.trace("send 401 on CustomAuthenticationFailureHandler handler");
        }
        if (this.defaultFailureUrl == null) {
            this.logger.debug("CustomAuthenticationFailureHandler.defaultFailureUrl is null");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        } else {
            this.saveException(request, exception);
            String message = "";
            if (exception instanceof BadCredentialsException) {
                message = "用户名或密码不正确！";
            } else if (exception instanceof LockedException) {
                message = "账号锁定！";
            } else if (exception instanceof DisabledException) {
                message = "账号被禁用！";
            } else if (exception instanceof AccountExpiredException) {
                message = "账号已过期！";
            } else if (exception instanceof UsernameNotFoundException) {
                message = "账号不存在，请联系工作人员创建账号！";
            }

            this.logger.debug("Redirecting to " + this.defaultFailureUrl);
            this.redirectStrategy.sendRedirect(request, response, this.defaultFailureUrl + "?err_msg=" + URLEncoder.encode(message, "utf-8"));
        }
    }

    protected final void saveException(HttpServletRequest request, AuthenticationException exception) {
        if (this.forwardToDestination) {
            request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null || this.allowSessionCreation) {
                request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
            }

        }
    }

    public boolean isForwardToDestination() {
        return forwardToDestination;
    }

    public void setForwardToDestination(boolean forwardToDestination) {
        this.forwardToDestination = forwardToDestination;
    }

    public boolean isAllowSessionCreation() {
        return allowSessionCreation;
    }

    public void setAllowSessionCreation(boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }
}
