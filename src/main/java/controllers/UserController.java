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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "user/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "user/{value}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getUser(@PathVariable long value) {
        return userService.getUserWithId(value);
    }

    @PutMapping("v2/user/")
    public ResponseEntity updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
     * Cache example
     * =================================================================================
     * */
    @GetMapping(value = "user/firstUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Cacheable(value = "employeeID1")
    public ResponseEntity getCachedUser() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(userService.getUserWithId(1));
    }

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

    @GetMapping(value = "v2/user/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAllUsersV2() {
        return userService.getAllUsersV2();
    }

    /*
     * HATEOAS example version 2
     * */

    @GetMapping(value = "user/{value}/org", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getUserOrg(@PathVariable long value) {
        return userService.getUserWithId(value);
    }

    @GetMapping(value = "v2/user/{value}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserWithLinks getUserLinks(@PathVariable long value) {
        User user = userService.getUserWithId(value);
        UserWithLinks linkedUser = new UserWithLinks(user);
        linkedUser.add(linkTo(methodOn(UserController.class).getUserLinks(value)).withSelfRel());
        linkedUser.add(linkTo(methodOn(UserController.class).getUserOrg(value)).withRel("Get_users_organization"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Update_with_PUT_method"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Delete_with_DELETE_method"));
        return linkedUser;
    }

    /*
     * Allowed methods
     * */

    @DeleteMapping("v2/user/{id}")
    public ResponseEntity deleteUser(@PathVariable long id) {
        try{
            userService.deleteUser(id);
        } catch (NoSuchElementException e) {
            //NOOP
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "v2/user/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
