package org.pet.project.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.SessionDAO;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionCheckService {

    private final SessionDAO sessionDAO;

    public Optional<User> getAuthenticatedUser(HttpServletRequest req, Model model) {
        log.info("Finding cookie with session id");
        Cookie[] cookies = req.getCookies();
        Optional<Cookie> cookie = findCookieByName(cookies, "sessionId");

        if (cookie.isEmpty()) {
            model.addAttribute("isAuthenticated", false);
            return Optional.empty();
        }

        log.info("Finding session with UUID: " + cookie.get().getValue());
        UUID sessionId = UUID.fromString(cookie.get().getValue());
        Optional<Session> session = sessionDAO.findById(sessionId);

        if (session.isEmpty() || session.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("error", "Ваша сессия истекла, пожалуйста, авторизируйтесь заново");
            return Optional.empty();
        }

        log.info("Finding user by session id");
        return Optional.of(session.get().getUser());
    }

    private Optional<Cookie> findCookieByName(Cookie[] cookies, String cookieName) {

        if (cookies == null || cookies.length < 1) {
            return Optional.empty();
        }

        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findFirst();
    }
}
