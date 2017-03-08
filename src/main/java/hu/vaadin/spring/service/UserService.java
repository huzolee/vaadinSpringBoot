package hu.vaadin.spring.service;

import com.vaadin.spring.annotation.SpringComponent;
import hu.vaadin.spring.dto.UserDTO;
import static hu.vaadin.spring.enumeration.Role.ROLE_ADMIN;
import hu.vaadin.spring.model.User;
import hu.vaadin.spring.repository.UserRepository;
import static hu.vaadin.spring.util.SessionAttribute.USER_DATA_ATTR_NAME;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@gmail.com>
 */
@Slf4j
@SpringComponent
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;

    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder, final HttpSession httpSession) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.httpSession = httpSession;
    }

    @Override
    public UserDetails loadUserByUsername(final String name) {
        final User user = userRepository.findByName(name);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }

        return createUser(user);
    }

    private UserDetails createUser(final User user) {
        log.info("authentication success for " + user);

        httpSession.setAttribute(USER_DATA_ATTR_NAME.getName(), user);
        return new org.springframework.security.core.userdetails.User(user.getName(),
                user.getPassword(), Collections.singleton(new SimpleGrantedAuthority(user.getUserRole())));
    }

    public User saveUser(final User user) {
        log.info("save user: " + user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername(final String username) {
        log.info("findByUsername(" + username + ")");
        return userRepository.findByName(username);
    }

    public User findByEmailAddress(final String emailAddress) {
        log.info("findByEmailAddress(" + emailAddress + ")");
        return userRepository.findByEmail(emailAddress);
    }

    public User findByFacebookID(final String facebookId) {
        log.info("findByFacebookID(" + facebookId + ")");
        return userRepository.findByFacebookId(facebookId);
    }

    @PostConstruct
    protected void initialize() {
        final UserDTO userDTO = new UserDTO();
        userDTO.setName("teszt");
        userDTO.setPassword("teszt");
        userDTO.setConfirmPassword("teszt");
        userDTO.setEmail("teszt@email.com");
        userDTO.setConfirmEmail("teszt@email.com");
        userDTO.setUserRole(ROLE_ADMIN);

        final User user = new User(userDTO);

        log.info("test user initialized: " + user);

        saveUser(user);
        log.info("test user saved");
    }
}
