package org.pet.project.controller;


import com.password4j.Hash;
import com.password4j.Password;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.project.controller.authentication.AuthController;
import org.pet.project.dao.SessionDao;
import org.pet.project.dao.UserDao;
import org.pet.project.model.dto.authentication.AuthFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.pet.project.service.UserSessionService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUT {

    @Mock
    private UserDao userDAO;

    @Mock
    private SessionDao sessionDAO;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private AuthController controller;


    @Test
    void notValidData() {

        when(bindingResult.hasErrors()).thenReturn(true);

        assertTrue(bindingResult.hasErrors());
        verify(sessionDAO, never()).save(any(Session.class));
        verify(resp, never()).addCookie(any(Cookie.class));
    }

    @Test
    void signIn_withValidationErrors_returnsTheSameForm() {
        AuthFormDto formDto = new AuthFormDto("not", "valid");

        when(bindingResult.hasErrors()).thenReturn(true);

        String controllerAnswer = controller.signIn(formDto, bindingResult, model, resp);

        assertEquals("sign-in", controllerAnswer);
        verify(sessionDAO, never()).save(any(Session.class));
        verify(resp, never()).addCookie(any(Cookie.class));
    }

    @Test
    void UserNotFoundByLogin() {
        AuthFormDto formDto = new AuthFormDto("login", "password");
        User user = new User("login", "password");

        when(userDAO.fingByLogin(user.getLogin())).thenReturn(Optional.empty());
        when(bindingResult.hasErrors()).thenReturn(false);

        String controllerAnswer = controller.signIn(formDto, bindingResult, model, resp);

        assertEquals("sign-in", controllerAnswer);
        verify(model).addAttribute("error", "Неверный логин или пароль");
        verify(sessionDAO, never()).save(any(Session.class));
        verify(resp, never()).addCookie(any(Cookie.class));
    }

    @Test
    void signIn_Success() {
        AuthFormDto formDto = new AuthFormDto("user", "password");
        Hash hash = Password.hash("password").withBcrypt();
        User user = new User("user", hash.getResult());

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDAO.fingByLogin(formDto.getLogin())).thenReturn(Optional.of(user));

        String controllerAnswer = controller.signIn(formDto, bindingResult, model, resp);

        verify(userSessionService).authUser(user, resp);

        assertEquals("redirect:/", controllerAnswer);
    }
}