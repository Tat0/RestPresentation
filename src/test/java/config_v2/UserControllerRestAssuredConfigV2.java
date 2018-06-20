package config_v2;

import controllers.GreetingController;
import controllers.UserController;
import exceptions.advice.ControllerExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.UserService;

@Configuration
public class UserControllerRestAssuredConfigV2 {

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
