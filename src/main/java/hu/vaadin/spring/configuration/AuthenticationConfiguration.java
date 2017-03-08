package hu.vaadin.spring.configuration;

import hu.vaadin.spring.model.User;
import hu.vaadin.spring.service.UserService;
import hu.vaadin.spring.util.FBUser;
import hu.vaadin.spring.util.FBUserData;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.vaadin.spring.security.config.AuthenticationManagerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import static hu.vaadin.spring.util.SessionAttribute.FB_DATA_ATTR_NAME;
import static hu.vaadin.spring.util.SessionAttribute.USER_DATA_ATTR_NAME;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@Configuration
public class AuthenticationConfiguration implements AuthenticationManagerConfigurer, ApplicationListener<ApplicationEvent> {

    @Autowired
    private UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FBUser fbUser;
    private final HttpSession httpSession;

    public AuthenticationConfiguration(final PasswordEncoder passwordEncoder,
            final FBUser fbUser, final HttpSession httpSession) {
        this.passwordEncoder = passwordEncoder;
        this.fbUser = fbUser;
        this.httpSession = httpSession;
    }

    @Override
    public void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event instanceof InteractiveAuthenticationSuccessEvent) {
            final User userProfile;
            final FBUserData fbProfile = fbUser.getProfile();

            httpSession.setAttribute(FB_DATA_ATTR_NAME.getName(), fbProfile);
            userProfile = userService.findByFacebookID(fbProfile.getId());

            if (userProfile != null) {
                httpSession.setAttribute(USER_DATA_ATTR_NAME.getName(), userProfile);
            }
        }
    }
}
