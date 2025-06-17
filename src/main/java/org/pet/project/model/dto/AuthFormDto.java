package org.pet.project.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthFormDto {

    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = ".*[a-zA-Zа-яА-Я]+.*", message = "Логин должен содержать хотя бы одну букву")
    private String login;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 6, message = "Пароль должен быть не короче 6 символов")
    private String password;
}
