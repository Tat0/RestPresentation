package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.User;
import entities.UserWithLinks;
import exceptions.CreationException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
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

import static io.restassured.RestAssured.get;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
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
        String string = get("/user/all").asString();
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
        String string = get("/user/{id}", "1").asString();
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
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void updateNonExistentUser() throws Exception {
        User user = new User(7, "Ostap", "Developer", true);
        BDDMockito.given(userService.updateUser(user)).willThrow(new NoSuchElementException("No value present"));
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(new ObjectMapper().writeValueAsString(user))
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("status", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("No value present"))
                .body("systemError", equalTo("500: Internal Server Error. Request has failed. Error message: No value present"))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

    @Test
    public void updateUserWithEmptyBody() {
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("")
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void updateUserWithIncorrectContentType() throws Exception {
        User testUser = userList.get(0);
        RestAssured.given()
                .contentType(MediaType.TEXT_PLAIN.toString())
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    public void getCachedUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        Response response = get("/user/firstUser");
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .header("Cache-Control", "max-age=60");
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(1)), response.getBody().asString());
    }

    @Test
    public void clearCache() throws Exception {
        User testUser = userList.get(0);
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("user/firstUser").then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void clearCacheWithIncorrectContentType() throws Exception {
        User testUser = userList.get(0);
        RestAssured.given()
                .contentType(MediaType.TEXT_PLAIN.toString())
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("/user/firstUser").then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    public void clearCacheWithEmptyBody() {
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("")
                .when().put("user/firstUser").then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void getAllUsersV2() throws Exception {
        BDDMockito.given(userService.getAllUsersV2()).willReturn(userList);
        Response response = get("/v2/user/all");
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        assertEquals(new ObjectMapper().writeValueAsString(userList), response.getBody().asString());
    }

    @Test
    public void getUserOrg() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(0));
        Response response = get("/user/{value}/org", 1);
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(0)), response.getBody().asString());
    }

    @Test
    public void getUserLinks() throws Exception {
        UserWithLinks linkedUser = new UserWithLinks(userList.get(1));
        linkedUser.add(linkTo(methodOn(UserController.class).getUserLinks(1)).withSelfRel());
        linkedUser.add(linkTo(methodOn(UserController.class).getUserOrg(1)).withRel("Get users organization"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Update with PUT method"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Delete with DELETE method"));
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        Response response = get("/v2/user/{value}", 1);
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
    public void deleteNonExistentUser() {
        BDDMockito.doThrow(new NoSuchElementException("No value present")).when(userService).deleteUser(7);
        RestAssured.delete("/v2/user/{id}", 7).then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("status", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("No value present"))
                .body("systemError", equalTo("500: Internal Server Error. Request has failed. Error message: No value present"))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

    @Test
    public void createUser() throws Exception {
        User user = new User(7, "Ostap", "Developer", true);
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(new ObjectMapper().writeValueAsString(user))
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void createUserWithEmptyBody() {
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("")
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void createUserWithIncorrectContentType() throws Exception {
        User user = new User(7, "Ostap", "Developer", true);
        RestAssured.given()
                .contentType(MediaType.TEXT_PLAIN.toString())
                .body(new ObjectMapper().writeValueAsString(user))
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    public void createUserWithAlredyExistentId() throws Exception {
        User user = new User(6, "Ostap", "Developer", true);
        BDDMockito.doThrow(new CreationException("Element with current id alredy exists.")).when(userService).createUser(user);
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(new ObjectMapper().writeValueAsString(user))
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("status", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("Element with current id alredy exists."))
                .body("systemError", equalTo("500: Internal Server Error. Request has failed. Error message: Element with current id alredy exists."))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

    @Test
    public void findByActivityAndRole() {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        RestAssured.when().
                get("/user/all").then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("findAll { it.active == true }.role", hasItems("Chief", "Homeless"));
    }

    @Test
    public void userSchemaValidation() {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(0));
        get("/user/{id}", "1").then().assertThat().body(matchesJsonSchemaInClasspath("validation/user-validator.json"));
    }

    @Test
    public void userArraySchemaValidation() {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        get("/user/all").then().assertThat().body(matchesJsonSchemaInClasspath("validation/user-array-validator.json"));
    }
}
