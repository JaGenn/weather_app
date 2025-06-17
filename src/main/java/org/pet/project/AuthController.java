package org.pet.project;


import com.password4j.Hash;
import com.password4j.Password;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.SessionDAO;
import org.pet.project.dao.UserDAO;
import org.pet.project.model.dto.AuthFormDto;
import org.pet.project.model.dto.RegistrationFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;


    @GetMapping("/sign-in")
    public String authForm(Model model) {
        model.addAttribute("authForm", new AuthFormDto());
        return "sign-in";
    }


    @PostMapping("/sign-in")
    public String signIn(@Valid @ModelAttribute("authForm") AuthFormDto form,
                         BindingResult result, Model model,
                         HttpServletResponse resp) {

        if (result.hasErrors()) {
            return "sign-in";
        }

        User user = userDAO.fingByLogin(form.getLogin()).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Пользователь с логином " + form.getLogin() + " не найден");
            return "sign-in";
        }
        if (!Password.check(form.getPassword(), user.getPassword()).withBcrypt()) {
            model.addAttribute("error", "Неправильный пароль для пользователя: " + form.getLogin());
            return "sign-in";
        }

        log.info("Creating new session");
        Session session = new Session(UUID.randomUUID(), user, LocalDateTime.now().plusHours(2));
        sessionDAO.save(session);

        log.info("Adding cookie with session: " + session.getId() + " to the response");
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);

        log.info("Authentication is successful: redirecting to the main page");
        return "redirect:/";
    }

}
