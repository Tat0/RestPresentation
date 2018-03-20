package configs.OAuth2;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService{

    private ServerUser user = new ServerUser(1L, "vloto@some.com", "$2a$10$D4OLKI6yy68crm.3imC9X.P2xqKHs5TloWUcr6z5XdOqnTrAK84ri", true);

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return user;
    }
}
