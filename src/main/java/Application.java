import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//uncomment to get OAuth2
//@EnableResourceServer
@ComponentScan(basePackages = {"controllers", "services"})
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
