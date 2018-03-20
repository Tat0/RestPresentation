import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
//uncomment to get OAuth2
@EnableResourceServer
@ComponentScan("controllers, configs, services")
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
