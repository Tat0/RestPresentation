package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.User;
import exceptions.NoValuesException;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserControllerUnitTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerRestAssuredTest {
    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    private static List<User> userList = new ArrayList<>();

    @BeforeClass
    public static void init() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";

        userList.add(new User(1, "Vitalii", "Chief", true));
        userList.add(new User(2, "Volodya", "Chief", true));
        userList.add(new User(3, "Petro", "Developer", false));
        userList.add(new User(4, "Oleg", "Manager", false));
        userList.add(new User(5, "Nazar", "Homeless", true));
        userList.add(new User(6, "Adam", "Homeless", true));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        RestAssured.when().get("/user/all").then()
                .statusCode(HttpStatus.OK.value())
                .contentType("application/json;charset=UTF-8")
                .body("userName", hasItems("Vitalii", "Volodya", "Petro", "Oleg", "Nazar", "Adam"))
                .body("userName", iterableWithSize(6));
        String string = RestAssured.get("/user/all").asString();
        assertEquals(new ObjectMapper().writeValueAsString(userList), string);
    }

    @Test
    public void testGetAllUsersOnEmptyList() throws Exception {
        BDDMockito.given(userService.getAllUsers()).willThrow(new NoValuesException("List is empty"));
        RestAssured.when().get("/user/all").then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType("application/json;charset=UTF-8")
                .body("status", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("List is empty"));
    }

    @Test
    public void testGetUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(0));
        RestAssured.when().get("/user/{id}", "1").then()
                .statusCode(HttpStatus.OK.value())
                .contentType("application/json;charset=UTF-8")
                .body("userName", equalTo("Vitalii"))
                .body("role", equalTo("Chief"))
                .body("active", equalTo(true))
                .body("id", equalTo(1));
        String string = RestAssured.get("/user/{id}", "1").asString();
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(0)), string);
    }

    @Test
    public void testGetNonExistentUser() {
        BDDMockito.given(userService.getUserWithId(1)).willThrow(new NoSuchElementException("No value present"));
        RestAssured.when().get("/user/{id}", "1").then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType("application/json;charset=UTF-8")
                .body("status", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("No value present"));
    }

    @Test
    public void testUpdateUser() throws Exception{
        User testUser = userList.get(0);
        testUser.setRole("Developer");
        BDDMockito.given(userService.updateUser(testUser)).willReturn(testUser);
        RestAssured.given()
                .contentType("application/json")
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.OK.value())
                .contentType("application/json;charset=UTF-8");
    }
}
