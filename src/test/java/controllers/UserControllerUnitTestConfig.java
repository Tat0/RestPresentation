package controllers;

import exceptions.advice.ControllerExceptionHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.UserService;

@Configuration
@EnableAutoConfiguration
public class UserControllerUnitTestConfig {
    @Bean
    public UserController userController() {
        return new UserController();
    }

    @Bean
    public GreetingController greetingController() {
        return new GreetingController();
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
