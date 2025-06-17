package org.pet.project;

import com.password4j.Hash;
import com.password4j.Password;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.SessionDAO;
import org.pet.project.dao.UserDAO;
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
public class RegisterController {

    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;


    @GetMapping("/sign-up")
    public String registrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationFormDto());
        return "sign-up";
    }


    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute("registrationForm") RegistrationFormDto form, BindingResult result,
                         Model model, HttpServletResponse resp) {

        if (result.hasErrors()) {
            return "sign-up";
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Пароли не совпадают!");
            return "sign-up";
        }

        Hash hash = Password.hash(form.getPassword()).withBcrypt();

        log.info("Saving new user to the database");
        User user = new User(form.getLogin(), hash.getResult());
        userDAO.save(user);

        log.info("Creating new session");
        Session session = new Session(UUID.randomUUID(), user, LocalDateTime.now().plusHours(2));
        sessionDAO.save(session);

        log.info("Adding cookie with session: " + session.getId() + " to the response");
        Cookie cookie = new Cookie("sessionId", session.getId().toString());
        resp.addCookie(cookie);

        log.info("Registration is successful: redirecting to the main page");
        return "redirect:/";
    }
}
