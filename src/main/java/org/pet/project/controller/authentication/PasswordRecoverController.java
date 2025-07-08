package org.pet.project.controller.authentication;

import com.password4j.Hash;
import com.password4j.Password;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pet.project.dao.UserDao;
import org.pet.project.model.dto.authentication.RegistrationFormDto;
import org.pet.project.model.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


@Slf4j
@Controller
@RequiredArgsConstructor
public class PasswordRecoverController {

    private final UserDao userDao;

    @GetMapping("/forgot-password")
    public String registrationForm(Model model) {
        addPageDataToModel(model);
        model.addAttribute("registrationForm", new RegistrationFormDto());
        return "sign-up";
    }

    @PostMapping("/forgot-password")
    public String signUp(@Valid @ModelAttribute("registrationForm") RegistrationFormDto form,
                         BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {

        addPageDataToModel(model);

        if (result.hasErrors()) {
            return "sign-up";
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Пароли не совпадают!");
            return "sign-up";
        }

        Optional<User> optionalUser = userDao.fingByLogin(form.getLogin());

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Такой пользователь не зарегистрирован");
            return "sign-up";
        }

        User user = optionalUser.get();

        if (Password.check(form.getPassword(), user.getPassword()).withBcrypt()) {
            model.addAttribute("error", "Этот пароль уже использовался. Придумайте новый.");
            return "sign-up";
        }

        Hash password = Password.hash(form.getPassword()).withBcrypt();
        user.setPassword(password.getResult());

        log.info("Saving new password to user " + user.getLogin());
        userDao.update(user);


        log.info("Password recover is successful: redirecting to the auth page");
        redirectAttributes.addFlashAttribute("success", "Ваш пароль был успешно обновлен");
        return "redirect:/sign-in";
    }

    private void addPageDataToModel(Model model) {
        model.addAttribute("formAction", "/forgot-password");
        model.addAttribute("title", "Восстановление пароля");
        model.addAttribute("subtitle", "Придумайте новый пароль");
        model.addAttribute("buttonMean","Обновить пароль");
    }
}
