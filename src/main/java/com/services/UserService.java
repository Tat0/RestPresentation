package com.services;

import com.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static List<User> userList = new ArrayList<>();
    private static AtomicLong counter = new AtomicLong();
    static {
        userList.add(new User(counter.incrementAndGet(), "Vitalii", "Chief", true));
        userList.add(new User(counter.incrementAndGet(), "Volodya", "Chief", true));
        userList.add(new User(counter.incrementAndGet(), "Petro", "Developer", false));
        userList.add(new User(counter.incrementAndGet(), "Oleg", "Manager", false));
        userList.add(new User(counter.incrementAndGet(), "Nazar", "Homeless", true));
        userList.add(new User(counter.incrementAndGet(), "Adam", "Homeless", true));
    }

    public List<User> getAllUsers() {
        return userList;
    }

    public List<User> getAllUsersV2() {
        return userList.stream().sorted( Comparator.comparing(User::getUserName)).collect(Collectors.toList());
    }


    public User getUserWithId(long id) {
        return userList.stream().filter(u -> u.getUserId() == id).findFirst().get();
    }

    public void createUser(User user) {
        userList.add((int) user.getUserId(), user);
    }

    public void updateUser(User user) {
        User nativeUser = userList.stream().filter(u -> u.getUserId() == user.getUserId()).findFirst().get();
        nativeUser.setUserName(user.getUserName());
        nativeUser.setRole(user.getRole());
        nativeUser.setActive(user.isActive());
    }

    public void deleteUser(long id) {
        userList.remove(id);
    }
}
