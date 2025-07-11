package org.pet.project.controller;


import com.password4j.Hash;
import com.password4j.Password;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pet.project.config.DataBaseConfig;
import org.pet.project.dao.UserDao;
import org.pet.project.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DataBaseConfig.class)
@WebAppConfiguration
public class RegisterControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDao userDAO;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Optional<User> optionalUser = userDAO.fingByLogin("tester");

        if (optionalUser.isEmpty()) {

            Hash password = Password.hash("password").withBcrypt();

            User user = new User("tester", password.getResult());
            userDAO.save(user);
        }
    }

    @Test
    @SneakyThrows
    void signUp_Success() {
        mockMvc.perform(post("/sign-up")
                .param("login", "new")
                .param("password", "password")
                .param("confirmPassword", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("sessionId"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @SneakyThrows
    void whenPasswordsAreNotSame() {
        mockMvc.perform(post("/sign-up")
                .param("login", "new")
                .param("password", "not")
                .param("confirmPassword", "same"))
                .andExpect(status().isOk())
                .andExpect(view().name("sign-up"));
    }

    @Test
    @SneakyThrows
    void whenUserAlreadyExists() {
        mockMvc.perform(post("/sign-up")
                .param("login", "tester")
                .param("password", "password")
                .param("confirmPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("sign-up"));
    }
}
