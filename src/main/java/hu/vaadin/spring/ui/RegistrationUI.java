package hu.vaadin.spring.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import static hu.vaadin.spring.enumeration.Path.REGISTRATION;
import hu.vaadin.spring.util.SessionAttribute;
import hu.vaadin.spring.view.RegistrationView;
import hu.vaadin.spring.service.UserService;
import hu.vaadin.spring.util.FBUserData;
import hu.vaadin.spring.util.Util;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.i18n.support.TranslatableUI;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@Theme("mytheme")
@SpringUI(path = REGISTRATION)
public class RegistrationUI extends TranslatableUI {

    @Autowired
    private RegistrationView registrationView;
    @Autowired
    private UserService userService;
    @Autowired
    private Util util;
    @Getter
    private String contextPath;

    @Override
    protected void initUI(final VaadinRequest vaadinRequest) {
        final FBUserData fbUserProfile = SessionAttribute.get(SessionAttribute.FB_DATA_ATTR_NAME);
        contextPath = vaadinRequest.getContextPath();

        if (util.isAnonymous() || (fbUserProfile != null && userService.findByFacebookID(fbUserProfile.getId()) == null)) {
            if (fbUserProfile != null) {
                registrationView.setFBUserProfile(fbUserProfile);
            }

            setContent(registrationView);
        } else {
            getUI().getPage().setLocation(new ExternalResource(vaadinRequest.getContextPath()).getURL());
        }
    }
}
