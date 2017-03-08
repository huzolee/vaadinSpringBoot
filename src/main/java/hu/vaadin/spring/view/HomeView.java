package hu.vaadin.spring.view;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import static hu.vaadin.spring.enumeration.Path.REGISTRATION;
import static hu.vaadin.spring.enumeration.Role.ROLE_ADMIN;
import static hu.vaadin.spring.enumeration.Role.ROLE_USER;
import static hu.vaadin.spring.enumeration.View.HOME_VIEW_NAME;
import hu.vaadin.spring.model.User;
import hu.vaadin.spring.util.SessionAttribute;
import hu.vaadin.spring.service.UserService;
import hu.vaadin.spring.ui.MyVaadinUI;
import hu.vaadin.spring.util.FBUserData;
import hu.vaadin.spring.util.Util;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.i18n.I18N;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@Slf4j
@Scope("request")
@SpringView(name = HOME_VIEW_NAME)
@Secured(value = {ROLE_USER, ROLE_ADMIN})
public class HomeView extends VerticalLayout implements IAbstractView {

    private final UserService userService;
    private final Util util;
    private final I18N i18n;

    public HomeView(final I18N i18n, final UserService userService, final Util util) {
        this.userService = userService;
        this.util = util;
        this.i18n = i18n;
    }

    @Override
    public void enter(final ViewChangeEvent event) {
        log.info("entered to: " + HOME_VIEW_NAME + " view");
        final User userProfile = SessionAttribute.get(SessionAttribute.USER_DATA_ATTR_NAME);
        final FBUserData fbUserProfile = SessionAttribute.get(SessionAttribute.FB_DATA_ATTR_NAME);

        if (fbUserProfile != null && userService.findByFacebookID(fbUserProfile.getId()) == null) {
            log.info("facebook profile registration");
            final MyVaadinUI myVaadinUI = util.getCurrentUI(MyVaadinUI.class);
            getUI().getPage().setLocation(new ExternalResource(myVaadinUI.getContextPath() + REGISTRATION).getURL());
        }

        if (fbUserProfile != null) {
            addComponent(new Label(fbUserProfile.getEmail()));
            addComponent(new Label(fbUserProfile.getFirstName()));
            addComponent(new Label(fbUserProfile.getLastName()));
            addComponent(new Label(fbUserProfile.getName()));
            addComponent(new Label(fbUserProfile.getId()));
            addComponent(new Label(fbUserProfile.getPictureURL()));
        }
        
        if (userProfile != null) {
            addComponent(new Label(userProfile.getEmail()));
            addComponent(new Label(userProfile.getName()));
            addComponent(new Label(userProfile.getId().toString()));
            addComponent(new Label(userProfile.getFacebookId()));
            addComponent(new Label(userProfile.getUserRole()));
        }
    }

    @Override
    public void updateMessageStrings(final Locale locale) {
        log.info("localized messages updated to: " + locale);
    }
}
