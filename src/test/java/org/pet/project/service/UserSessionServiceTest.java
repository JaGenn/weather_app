package org.pet.project.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.pet.project.config.DataBaseConfig;
import org.pet.project.dao.SessionDao;
import org.pet.project.dao.UserDao;
import org.pet.project.model.dto.authentication.RegistrationFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DataBaseConfig.class)
@WebAppConfiguration
class UserSessionServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private UserSessionService userSessionService;

    @Test
    void register_Success() {

        RegistrationFormDto formDto = new RegistrationFormDto("login", "password", "password");

        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        userSessionService.registerUser(formDto, resp);

        Optional<User> user = userDao.fingByLogin(formDto.getLogin());
        assertTrue(user.isPresent());
        assertEquals("login", user.get().getLogin());

        verify(resp).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        assertEquals("sessionId", cookie.getName());

        Optional<Session> session = sessionDao.findById(UUID.fromString(cookie.getValue()));
        assertTrue(session.isPresent());
        assertEquals(session.get().getId().toString(), cookie.getValue());
        assertEquals(user.get().getId(), session.get().getUser().getId());

    }

}