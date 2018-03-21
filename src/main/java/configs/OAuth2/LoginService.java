package configs.OAuth2;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//@Service
public class LoginService implements UserDetailsService{

    private ServerUser user = new ServerUser(1L, "vloto",
            new BCryptPasswordEncoder().encode("password"), true);

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return user;
    }
}
