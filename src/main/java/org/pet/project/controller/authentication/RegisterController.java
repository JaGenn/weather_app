package org.pet.project.controller.authentication;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.UserDao;
import org.pet.project.exception.UserExistsException;
import org.pet.project.model.dto.authentication.RegistrationFormDto;
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
public class RegisterController {

    private final UserDao userDao;
    private final UserSessionService userSessionService;


    @GetMapping("/sign-up")
    public String registrationForm(Model model) {
        addPageDataToModel(model);
        model.addAttribute("registrationForm", new RegistrationFormDto());
        return "sign-up";
    }


    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute("registrationForm") RegistrationFormDto form, BindingResult result,
                         Model model, HttpServletResponse resp) {

        addPageDataToModel(model);

        if (result.hasErrors()) {
            return "sign-up";
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Пароли не совпадают!");
            return "sign-up";
        }

        try {
            userSessionService.registerUser(form, resp);
        } catch (UserExistsException e) {
            model.addAttribute("error", "Такой пользователь уже зарегистрирован.");
            return "sign-up";
        }

        log.info("Registration is successful: redirecting to the main page");
        return "redirect:/";
    }

    private void addPageDataToModel(Model model) {
        model.addAttribute("formAction", "/sign-up");
        model.addAttribute("title", "Регистрация");
        model.addAttribute("subtitle", "Создайте новый аккаунт");
        model.addAttribute("buttonMean","Зарегистрироваться");
    }
}
