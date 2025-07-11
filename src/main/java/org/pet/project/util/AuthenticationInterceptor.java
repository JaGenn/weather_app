package org.pet.project.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.SessionDao;
import org.pet.project.model.entity.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final SessionDao sessionDAO;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie cookie = WebUtils.getCookie(request, "sessionId");

        if (cookie == null) {
            request.setAttribute("user", null);
            request.setAttribute("isAuthenticated", false);
            return true;
        }

        try {
            UUID sessionId = UUID.fromString(cookie.getValue());
            Optional<Session> session = sessionDAO.findById(sessionId);

            if (session.isEmpty() || session.get().getExpiresAt().isBefore(LocalDateTime.now())) {
                request.setAttribute("user", null);
                request.setAttribute("isAuthenticated", false);
            } else {
                request.setAttribute("user", session.get().getUser());
                request.setAttribute("isAuthenticated", true);
            }

        } catch (IllegalArgumentException ex) {
            request.setAttribute("user", null);
            request.setAttribute("isAuthenticated", false);
        }

        return true;
    }
}
