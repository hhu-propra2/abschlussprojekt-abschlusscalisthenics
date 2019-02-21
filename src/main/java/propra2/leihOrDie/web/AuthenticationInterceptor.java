package propra2.leihOrDie.web;

import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import propra2.leihOrDie.dataaccess.SessionRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private CookieHandler cookieHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean pageRequiresLogin = true;

        if(request.getRequestURI().equals("/login") || request.getRequestURI().equals("/registration")) {
            pageRequiresLogin = false;
        }

        Cookie[] cookies = request.getCookies();
        if(cookieHandler.checkIfLoggedIn(cookies)) {
            if(!pageRequiresLogin) {
                response.sendRedirect("/");
                return false;
            }
            return true;
        }

        if(pageRequiresLogin) {
            response.sendRedirect("login");
            return false;
        }

        return true;
    }
}
