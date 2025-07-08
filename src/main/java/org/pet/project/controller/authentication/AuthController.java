package org.pet.project.controller.authentication;


import com.password4j.Password;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.UserDao;
import org.pet.project.model.dto.authentication.AuthFormDto;
import org.pet.project.model.entity.User;
import org.pet.project.service.UserSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;



@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserDao userDao;
    private final UserSessionService userSessionService;


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

        Optional<User> optionalUser = userDao.fingByLogin(form.getLogin());

        if (optionalUser.isEmpty() || !Password.check(form.getPassword(), optionalUser.get().getPassword()).withBcrypt()) {
            model.addAttribute("error", "Неверный логин или пароль");
            return "sign-in";
        }

        userSessionService.authUser(optionalUser.get(), resp);

        log.info("Authentication is successful: redirecting to the main page");
        return "redirect:/";
    }

    @PostMapping("/sign-out")
    public String signOut(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "main-page";
    }

}
