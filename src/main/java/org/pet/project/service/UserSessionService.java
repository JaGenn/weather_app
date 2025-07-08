package org.pet.project.service;

import com.password4j.Hash;
import com.password4j.Password;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.SessionDao;
import org.pet.project.dao.UserDao;
import org.pet.project.exception.UniqueConstraintViolationException;
import org.pet.project.exception.UserExistsException;
import org.pet.project.model.dto.authentication.RegistrationFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final SessionDao sessionDao;
    private final UserDao userDao;

    public void registerUser(RegistrationFormDto form, HttpServletResponse resp) {

        Hash password = Password.hash(form.getPassword()).withBcrypt();

        log.info("Saving new user to the database");
        User user = new User(form.getLogin(), password.getResult());

        try {
            userDao.save(user);
        } catch (UniqueConstraintViolationException e) {
            throw new UserExistsException(e.getMessage());
        }

        log.info("Creating new session");
        Session session = new Session(user, LocalDateTime.now().plusHours(24));
        sessionDao.save(session);

        log.info("Adding cookie with session: " + session.getId() + " to the response");
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);
    }

    public void authUser(User user, HttpServletResponse resp) {

        log.info("Creating new session");
        Session session = new Session(user, LocalDateTime.now().plusHours(24));
        sessionDao.save(session);

        log.info("Adding cookie with session: " + session.getId() + " to the response");
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);
    }
}
