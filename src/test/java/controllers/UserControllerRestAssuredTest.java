package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.User;
import entities.UserWithLinks;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
    }

    @Before
    public void setUp() {
        userList.add(new User(1, "Vitalii", "Chief", true));
        userList.add(new User(2, "Volodya", "Chief", true));
        userList.add(new User(3, "Petro", "Developer", false));
        userList.add(new User(4, "Oleg", "Manager", false));
        userList.add(new User(5, "Nazar", "Homeless", true));
        userList.add(new User(6, "Adam", "Homeless", true));
    }

    @After
    public void shutDown() {
        userList = new ArrayList<>();
    }

    @Test
    public void getAllUsers() throws Exception {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        RestAssured.when().get("/user/all").then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("userName", hasItems("Vitalii", "Volodya", "Petro", "Oleg", "Nazar", "Adam"))
                .body("userName", iterableWithSize(6));
        String string = RestAssured.get("/user/all").asString();
        assertEquals(new ObjectMapper().writeValueAsString(userList), string);
    }

    @Test
    public void getUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(0));
        RestAssured.when().get("/user/{id}", "1").then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("userName", equalTo("Vitalii"))
                .body("role", equalTo("Chief"))
                .body("active", equalTo(true))
                .body("id", equalTo(1));
        String string = RestAssured.get("/user/{id}", "1").asString();
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(0)), string);
    }

    @Test
    public void getNonExistentUser() {
        BDDMockito.given(userService.getUserWithId(1)).willThrow(new NoSuchElementException("No value present"));
        RestAssured.when().get("/user/{id}", "1").then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("status", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("No value present"));
    }

    @Test
    public void updateUser() throws Exception {
        User testUser = userList.get(0);
        testUser.setRole("Developer");
        RestAssured.given()
                .contentType("application/json")
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void getCachedUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        Response response = RestAssured.get("/user/firstUser");
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .header("Cache-Control", "max-age=60");
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(1)), response.getBody().asString());
    }

    @Test
    public void getAllUsersV2() throws Exception {
        BDDMockito.given(userService.getAllUsersV2()).willReturn(userList);
        Response response = RestAssured.get("/v2/user/all");
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        assertEquals(new ObjectMapper().writeValueAsString(userList), response.getBody().asString());
    }

    @Test
    public void getUserOrg() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(0));
        Response response = RestAssured.get("/user/{value}/org", 1);
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(0)), response.getBody().asString());
    }

    @Test
    public void getUserLinks() throws JsonProcessingException {
        UserWithLinks linkedUser = new UserWithLinks(userList.get(1));
        linkedUser.add(linkTo(methodOn(UserController.class).getUserLinks(1)).withSelfRel());
        linkedUser.add(linkTo(methodOn(UserController.class).getUserOrg(1)).withRel("Get users organization"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Update with PUT method"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Delete with DELETE method"));
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        Response response = RestAssured.get("/v2/user/{value}", 1);
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        assertEquals(new ObjectMapper().writeValueAsString(linkedUser), response.getBody().asString());
    }

    @Test
    public void deleteUser() {
        Response response = RestAssured.delete("/v2/user/{id}", 1);
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void createUser() throws Exception {
        RequestSpecification request = RestAssured.given();
        request.contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        request.body(new ObjectMapper().writeValueAsString(userList.get(0)));
        Response response = request.post("/v2/user/");
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
