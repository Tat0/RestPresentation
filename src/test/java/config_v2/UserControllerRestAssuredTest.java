package config_v2;


import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.UserController;
import entities.User;
import exceptions.RestException;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import services.UserService;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.get;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserControllerRestAssuredConfigV2.class)
public class UserControllerRestAssuredTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private List<User> userList = new ArrayList<>();

    @Before
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        userList.add(new User(1, "Vitalii", "Chief", true));
        userList.add(new User(2, "Volodya", "Chief", true));
        userList.add(new User(3, "Petro", "Developer", false));
        userList.add(new User(4, "Oleg", "Manager", false));
        userList.add(new User(5, "Nazar", "Developer", true));
        userList.add(new User(6, "Adam", "Human", true));
    }

    @After
    public void shutDown() {
        userList = new ArrayList<>();
    }

    @Test
    public void checkGettingAllUsers() throws Exception {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        MockMvcResponse response = get("/user/all");
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("userName", hasItems("Vitalii", "Volodya", "Petro", "Oleg", "Nazar", "Adam"))
                .body("userName", iterableWithSize(6))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/user-array-validator.json"));
        assertEquals(new ObjectMapper().writeValueAsString(userList), response.asString());
    }

    @Test
    public void checkGettingAllUsersWhenDatabaseFail() {
        BDDMockito.given(userService.getAllUsers()).willThrow(new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Connection refused"));
        MockMvcResponse response = get("/user/all");
        response.then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("status", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", equalTo("Connection refused"))
                .body("error", equalTo("500: Internal Server Error. System error: Connection refused"))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void checkGettingUser() {
        BDDMockito.given(userService.getUserWithId(1L)).willReturn(userList.get(0));
        MockMvcResponse response = get("/user/{id}", 1);
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("userName", equalTo("Vitalii"))
                .body("role", equalTo("Chief"))
                .body("active", equalTo(true))
                .body("id", equalTo(1))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/user-validator.json"));
    }

    @Test
    public void checkGettingUserWhenItNotExist() {
        BDDMockito.given(userService.getUserWithId(7L)).willThrow(new RestException(HttpStatus.NOT_FOUND, "No value present"));
        RestAssuredMockMvc.when().get("/user/{id}", 7).then()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo("NOT_FOUND"))
                .body("message", equalTo("No value present"))
                .body("error", equalTo("404: Not Found. System error: No value present"))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

    @Test
    public void checkUpdatingUserWhenMethodArgumentWasChanged() {
        RestAssuredMockMvc.when().get("/user/{id}", Long.MAX_VALUE).then()
                .statusCode(HttpStatus.OK.value());
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void checkUpdatingUser() {
        User testUser = userList.get(0);
        testUser.setRole("Developer");
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(testUser)
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void checkUpdatingUserWhenItNotExist() {
        User user = new User(7L, "Ostap", "Developer", true);
        BDDMockito.given(userService.updateUser(user)).willThrow(new RestException(HttpStatus.NOT_FOUND, "No value present"));
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(user)
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo("NOT_FOUND"))
                .body("message", equalTo("No value present"))
                .body("error", equalTo("404: Not Found. System error: No value present"))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

    @Test
    public void checkUpdatingUserWhenRequestBodyIsEmpty() {
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("")
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void checkUpdatingUserWhenContentTypeHeaderIsWrong() throws Exception {
        User testUser = userList.get(0);
        RestAssuredMockMvc.given()
                .contentType(MediaType.TEXT_PLAIN.toString())
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("/v2/user/").then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void checkGettingCachedUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1L)).willReturn(userList.get(1));
        MockMvcResponse response = get("/user/firstUser");
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .header("Cache-Control", "max-age=60");
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(1)), response.getBody().asString());
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void checkClearingCache() {
        User testUser = userList.get(0);
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(testUser)
                .when().put("user/firstUser").then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void checkClearingCacheWhenContentTypeHeaderIsWrong() throws Exception {
        User testUser = userList.get(0);
        RestAssuredMockMvc.given()
                .contentType(MediaType.TEXT_PLAIN.toString())
                .body(new ObjectMapper().writeValueAsString(testUser))
                .when().put("/user/firstUser").then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    public void checkClearingCacheWhenRequestBodyIsEmpty() {
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("")
                .when().put("user/firstUser").then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void checkGettingAllUsersV2() throws Exception {
        BDDMockito.given(userService.getAllUsersV2()).willReturn(userList);
        MockMvcResponse response = get("/v2/user/all");
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        assertEquals(new ObjectMapper().writeValueAsString(userList), response.getBody().asString());
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void checkGettingUserOrganisation() throws Exception {
        BDDMockito.given(userService.getUserWithId(1L)).willReturn(userList.get(0));
        MockMvcResponse response = get("/user/{value}/org", 1);
        response.then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString());
        assertEquals(new ObjectMapper().writeValueAsString(userList.get(0)), response.getBody().asString());
    }

//----------------------------------------------------------------------------------------------------------------------


    @Test
    public void checkGettingUsersLinksWhenItNotExist() {
        BDDMockito.given(userService.getUserWithId(7L)).willThrow(new RestException(HttpStatus.NOT_FOUND, "No value present"));
        get("/v2/user/{value}", 7).then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("status", equalTo("NOT_FOUND"))
                .body("message", equalTo("No value present"))
                .body("error", equalTo("404: Not Found. System error: No value present"))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

    @Test
    public void checkGettingUsersLinksWhenPathVariableIsNotValid() {
        get("/v2/user/{value}", "abc").then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

//----------------------------------------------------------------------------------------------------------------------


    @Test
    public void checkDeletingUser() {
        MockMvcResponse response = RestAssuredMockMvc.delete("/v2/user/{id}", 1);
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void checkDeletingUserWhenItNotExist() {
        RestAssuredMockMvc.delete("/v2/user/{id}", 7).then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void checkCreatingUser() throws Exception {
        User user = new User(7L, "Ostap", "Developer", true);
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(user)
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void checkCreatingUserWhenRequestBodyIsEmpty() {
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body("")
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void checkCreatingUserWhenContentTypeHeaderIsWrong() throws Exception {
        User user = new User(7L, "Ostap", "Developer", true);
        RestAssuredMockMvc.given()
                .contentType(MediaType.TEXT_PLAIN.toString())
                .body(new ObjectMapper().writeValueAsString(user))
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    public void checkCreatingUserWhenIdAlredyExist() throws Exception {
        User user = new User(6L, "Ostap", "Developer", true);
        BDDMockito.doThrow(new RestException(HttpStatus.BAD_REQUEST, "User with current id alredy exists."))
                .when(userService).createUser(user);
        RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_UTF8.toString())
                .body(user)
                .when().post("/v2/user/").then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo("BAD_REQUEST"))
                .body("message", equalTo("User with current id alredy exists."))
                .body("error", equalTo("400: Bad Request. System error: User with current id alredy exists."))
                .assertThat().body(matchesJsonSchemaInClasspath("validation/exception-validator.json"));
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void userSchemaValidation() {
        BDDMockito.given(userService.getUserWithId(1L)).willReturn(userList.get(0));
        get("/user/{id}", 1).then()
                .assertThat().body(matchesJsonSchemaInClasspath("validation/user-validator.json"));
    }

    @Test
    public void validateUsersArrayWithSchema() {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        get("/user/all").then()
                .assertThat().body(matchesJsonSchemaInClasspath("validation/user-array-validator.json"));
    }
}
