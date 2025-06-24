package org.pet.project.controller.authentication;


import com.password4j.Password;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.SessionDAO;
import org.pet.project.dao.UserDAO;
import org.pet.project.model.dto.authentication.AuthFormDto;
import org.pet.project.model.entity.Session;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.Optional;
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

        Optional<User> optionalUser = userDAO.fingByLogin(form.getLogin());

        if (optionalUser.isEmpty() || !Password.check(form.getPassword(), optionalUser.get().getPassword()).withBcrypt()) {
            model.addAttribute("error", "Неверный логин или пароль");
            return "sign-in";
        }

        log.info("Creating new session");
        Session session = new Session(UUID.randomUUID(), optionalUser.get(), LocalDateTime.now().plusHours(24));
        sessionDAO.save(session);

        log.info("Adding cookie with session: " + session.getId() + " to the response");
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);

        log.info("Authentication is successful: redirecting to the main page");
        return "redirect:/";
    }

    @PostMapping("/sign-out")
    public String signOut(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "main-page";
    }

}
