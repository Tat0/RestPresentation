package controllers;

import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserControllerUnitTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GreetingControllerTest {

    @Test
    public void greeting() {
        RestAssured.when().get("/greeting").then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("id", equalTo(1))
                .body("content", equalTo("Hello World"));
    }
}
