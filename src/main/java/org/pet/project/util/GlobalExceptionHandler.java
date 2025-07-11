package org.pet.project.util;

import jakarta.servlet.http.HttpServletRequest;
import org.pet.project.exception.CookieNotFoundException;
import org.pet.project.exception.DataBaseOperationException;
import org.pet.project.exception.LocationNotFoundException;
import org.pet.project.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public String weatherApiIsNotAvailable(LocationNotFoundException e, Model model, HttpServletRequest req) {
        putUserLoginToModel(req, model);
        model.addAttribute("searchQuery", req.getParameter("location"));
        model.addAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("errorMessage", e.getMessage());
        return "error-page";
    }

    @ExceptionHandler(DataBaseOperationException.class)
    public String dataBaseError(DataBaseOperationException e, Model model, HttpServletRequest req) {
        putUserLoginToModel(req, model);
        model.addAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("errorMessage", e.getMessage());
        return "error-page";
    }

    @ExceptionHandler(CookieNotFoundException.class)
    public String userNotFoundInCookie(CookieNotFoundException e, Model model) {
        model.addAttribute("errorCode", HttpStatus.UNAUTHORIZED);
        model.addAttribute("errorMessage", e.getMessage());
        return "error-page";
    }


    private void putUserLoginToModel(HttpServletRequest req, Model model) {
        User user = (User) req.getAttribute("user");
        if (user != null) {
            model.addAttribute("login", user.getLogin());
        }
    }
}
