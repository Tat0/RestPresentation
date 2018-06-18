package controllers;

import entities.User;
import entities.UserWithLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "user/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAllUsers() throws SQLException {
        return userService.getAllUsers();
    }

    //TODO Test Null Pointer
    //TODO Value is incorrect
    @GetMapping(value = "user/{value}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getUser(@PathVariable long value) {
        return userService.getUserWithId(value);
    }

    //TODO Test Null Pointer
    @PutMapping("v2/user/")
    public ResponseEntity updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
     * Cache example
     * =================================================================================
     * */

    //TODO Test Null Pointer
    @GetMapping(value = "user/firstUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Cacheable(value = "employeeID1")
    public ResponseEntity getCachedUser() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(userService.getUserWithId(1));
    }

    //TODO Test Null Pointer
    @PutMapping(value = "user/firstUser", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @CacheEvict(value = "employeeID1", allEntries = true)
    public ResponseEntity clearCache(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
     * Version example
     * =================================================================================
     * */

    //TODO Test null pointer
    @GetMapping(value = "v2/user/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAllUsersV2() {
        return userService.getAllUsersV2();
    }

    /*
     * HATEOAS example version 2
     * */

    //TODO Test null pointer
    //TODO Value is incorrect
    @GetMapping(value = "user/{value}/org", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getUserOrganisation(@PathVariable long value) {
        return userService.getUserWithId(value);
    }

    //TODO Test null pointer
    //TODO Value is incorrect
    @GetMapping(value = "v2/user/{value}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserWithLinks getUserLinks(@PathVariable long value) {
        User user = userService.getUserWithId(value);
        UserWithLinks linkedUser = new UserWithLinks(user);
        linkedUser.add(linkTo(methodOn(UserController.class).getUserLinks(value)).withSelfRel());
        linkedUser.add(linkTo(methodOn(UserController.class).getUserOrganisation(value)).withRel("Get_users_organization"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Update_with_PUT_method"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Delete_with_DELETE_method"));
        return linkedUser;
    }

    /*
     * Allowed methods
     * */

    //TODO Test null pointer
    //TODO Value is incorrect
    @DeleteMapping("v2/user/{id}")
    public ResponseEntity deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //TODO Test null pointer
    @PostMapping(value = "v2/user/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
