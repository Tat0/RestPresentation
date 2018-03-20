package services;

import entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
    }

    public List<User> getAllUsers() {
        System.out.println("=======================================\n Imitating database");
        System.out.println("get data from database: " + userList);
        return userList;
    }
}
