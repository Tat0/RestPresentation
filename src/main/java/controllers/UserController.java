package controllers;

import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.UserService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("user/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
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
        return ResponseEntity.ok().build();
    }

    /*
     * Version example
     * =================================================================================
     * */

    @GetMapping("v2/user/all")
    public List<User> getAllUsersV2 (){
        return userService.getAllUsersV2();
    }
}
