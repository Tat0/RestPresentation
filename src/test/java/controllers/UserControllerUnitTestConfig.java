package controllers;

import exceptions.advice.ControllerExceptionHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import services.UserService;

@TestConfiguration
public class UserControllerUnitTestConfig {
    @Bean
    public UserController userController() {
        return new UserController();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public ControllerExceptionHandler controllerExceptionHandler() {
        return new ControllerExceptionHandler();
    }
}
