package org.pet.project.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.project.dao.SessionDAO;
import org.pet.project.dao.UserDAO;
import org.pet.project.model.dto.RegistrationFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
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
    private SessionDAO sessionDAO;

    @Mock
    private UserDAO userDAO;

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

        verify(userDAO).save(any(User.class));
        verify(sessionDAO).save(any(Session.class));
        verify(resp).addCookie(any(Cookie.class));
        assertEquals("redirect:/", answerView);

    }

    private boolean comparePasswords(RegistrationFormDto formDto) {
        return formDto.getPassword().equals(formDto.getConfirmPassword());
    }



}
