package org.pet.project.service;

import com.password4j.Hash;
import com.password4j.Password;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.SessionDAO;
import org.pet.project.dao.UserDAO;
import org.pet.project.model.dto.authentication.RegistrationFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final SessionDAO sessionDAO;
    private final UserDAO userDAO;

    public void registerUser(RegistrationFormDto form, HttpServletResponse resp) {

        Hash password = Password.hash(form.getPassword()).withBcrypt();

        log.info("Saving new user to the database");
        User user = new User(form.getLogin(), password.getResult());
        userDAO.save(user);

        log.info("Creating new session");
        Session session = new Session(user, LocalDateTime.now().plusHours(24));
        sessionDAO.save(session);

        log.info("Adding cookie with session: " + session.getId() + " to the response");
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);
    }

    public void authUser(User user, HttpServletResponse resp) {

        log.info("Creating new session");
        Session session = new Session(user, LocalDateTime.now().plusHours(24));
        sessionDAO.save(session);

        log.info("Adding cookie with session: " + session.getId() + " to the response");
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);
    }
}
