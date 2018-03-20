package services;

import configs.ServerUser;
import entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService implements UserDetailsService {

    private static List<User> userList = new ArrayList<>();
    private static AtomicLong counter = new AtomicLong();
    static {
        userList.add(new User(counter.incrementAndGet(), "Vitalii", "Chief", true));
        userList.add(new User(counter.incrementAndGet(), "Volodya", "Chief", true));
        userList.add(new User(counter.incrementAndGet(), "Petro", "Developer", false));
        userList.add(new User(counter.incrementAndGet(), "Oleg", "Manager", false));
        userList.add(new User(counter.incrementAndGet(), "Nazar", "Homeless", true));
    }

    private ServerUser user = new ServerUser(1L, "vloto@some.com", "$2a$10$D4OLKI6yy68crm.3imC9X.P2xqKHs5TloWUcr6z5XdOqnTrAK84ri", true);

    public List<User> getAllUsers() {
        System.out.println("=======================================\n Imitating database");
        System.out.println("get data from database: " + userList);
        return userList;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return s.equals(user.getUsername()) ? user : null;
    }
}
