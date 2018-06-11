package controllers;

import entities.User;
import entities.UserWithLinks;
import services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("user/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("user/{value}")
    public User getUser(@PathVariable long value) {
        return userService.getUserWithId(value);
    }

    @PutMapping("v2/user/")
    public ResponseEntity updateUser(@RequestBody User user){
        userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
    * Cache example
    * =================================================================================
    * */
    @GetMapping("user/firstUser")
    @Cacheable(value = "employeeID1")
    public ResponseEntity getCachedUser(){
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(userService.getUserWithId(1));
    }

    @PutMapping("user/firstUser")
    @CacheEvict(value = "employeeID1", allEntries = true)
    public ResponseEntity clearCache(@RequestBody User user){
        userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
     * Version example
     * =================================================================================
     * */

    @GetMapping("v2/user/all")
    public List<User> getAllUsersV2 (){
        return userService.getAllUsersV2();
    }

    /*
    * HATEOAS example version 2
    * */

    @GetMapping("user/{value}/org")
    public User getUserOrg(@PathVariable long value) {
        return userService.getUserWithId(value);
    }

    @GetMapping("v2/user/{value}")
    public UserWithLinks getUserLinks(@PathVariable long value) {
        User user = userService.getUserWithId(value);
        UserWithLinks linkedUser = new UserWithLinks(user);
        linkedUser.add(linkTo(methodOn(UserController.class).getUserLinks(value)).withSelfRel());
        linkedUser.add(linkTo(methodOn(UserController.class).getUserOrg(value)).withRel("Get users organization"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Update with PUT method"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Delete with DELETE method"));
        return linkedUser;
    }

    /*
    * Allowed methods
    * */

    @DeleteMapping("v2/user/{id}")
    public ResponseEntity deleteUser(@PathVariable long id){
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("v2/user/")
    public ResponseEntity createUser(@RequestBody User user){
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
