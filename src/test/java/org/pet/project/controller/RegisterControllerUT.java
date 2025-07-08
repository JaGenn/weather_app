package org.pet.project.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.project.controller.authentication.RegisterController;
import org.pet.project.dao.SessionDao;
import org.pet.project.dao.UserDao;
import org.pet.project.model.dto.authentication.RegistrationFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.pet.project.service.UserSessionService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterControllerUT {

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private SessionDao sessionDAO;

    @Mock
    private UserDao userDAO;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private RegisterController controller;

    @Test
    void whenUserDataNotValid() {

        RegistrationFormDto formDto = new RegistrationFormDto("123", "1", "1");

        when(bindingResult.hasErrors()).thenReturn(true);
        assertTrue(bindingResult.hasErrors());
        verify(userDAO, never()).save(any(User.class));
        verify(sessionDAO, never()).save(any(Session.class));
        verify(resp, never()).addCookie(any(Cookie.class));

        String answerView = controller.signUp(formDto, bindingResult, model, resp);
        assertEquals("sign-up", answerView);
    }

    @Test
    void whenPasswordsAreNotSame() {
        RegistrationFormDto formDto = new RegistrationFormDto("login", "different", "passwords");

        when(bindingResult.hasErrors()).thenReturn(false);
        assertFalse(bindingResult.hasErrors());
        assertFalse(comparePasswords(formDto));

        String answerView = controller.signUp(formDto, bindingResult, model, resp);

        assertEquals("sign-up", answerView);
        verify(model).addAttribute("error", "Пароли не совпадают!");
        verify(userDAO, never()).save(any(User.class));
        verify(sessionDAO, never()).save(any(Session.class));
        verify(resp, never()).addCookie(any(Cookie.class));
    }

    @Test
    void signUp_Success() {
        RegistrationFormDto formDto = new RegistrationFormDto("login", "password", "password");

        when(bindingResult.hasErrors()).thenReturn(false);
        assertFalse(bindingResult.hasErrors());
        assertTrue(comparePasswords(formDto));

        String answerView = controller.signUp(formDto, bindingResult, model, resp);

        verify(userSessionService).registerUser(formDto, resp);

        assertEquals("redirect:/", answerView);

    }

    private boolean comparePasswords(RegistrationFormDto formDto) {
        return formDto.getPassword().equals(formDto.getConfirmPassword());
    }



}
