package hu.vaadin.spring.view;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import static hu.vaadin.spring.enumeration.Path.FB_LOGIN;
import static hu.vaadin.spring.enumeration.Path.REGISTRATION;
import static hu.vaadin.spring.enumeration.Role.ROLE_ANONYMOUS;
import static hu.vaadin.spring.enumeration.View.LOGIN_VIEW_NAME;
import hu.vaadin.spring.ui.LoginUI;
import hu.vaadin.spring.util.LangChooser;
import hu.vaadin.spring.util.Util;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.i18n.I18N;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@Slf4j
@Scope("request")
@Secured(value = ROLE_ANONYMOUS)
@SpringView(name = LOGIN_VIEW_NAME)
public class LoginView extends VerticalLayout implements IAbstractView {

    private final Button loginButton = new Button();
    private final Button fbLoginButton = new Button();
    private final Button registrationButton = new Button();
    private final Panel myFormContainer = new Panel();
    private final FormLayout myForm = new FormLayout();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final Util util;
    private final I18N i18n;
    private final LangChooser langChooser;

    public LoginView(final I18N i18n, final DaoAuthenticationProvider daoAuthenticationProvider,
            final Util util, final LangChooser langChooser) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.util = util;
        this.i18n = i18n;
        this.langChooser = langChooser;

        setSizeFull();
        makeFormContainer();
        addButtonClickListeners();
        addComponents(myFormContainer);

        log.info(LOGIN_VIEW_NAME + " view created");
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    private void addButtonClickListeners() {
        loginButton.addClickListener((final Button.ClickEvent event) -> {
            final String username = usernameField.getValue();
            final String password = passwordField.getValue();
            final Authentication auth = new UsernamePasswordAuthenticationToken(
                    username, password);

            try {
                final Authentication authenticated = daoAuthenticationProvider.authenticate(auth);
                
                SecurityContextHolder.getContext().setAuthentication(authenticated);

                final LoginUI loginUI = util.getCurrentUI(LoginUI.class);
                getUI().getPage().setLocation(new ExternalResource(loginUI.getContextPath()).getURL());
            } catch (final AuthenticationException aex) {
                final Notification notification = new Notification(i18n.get("signin.error.title"), i18n.get("signin.error.bad_login"));
                notification.show(Page.getCurrent());
                log.warn("authentication exception for " + auth + ": " + aex.getMessage());
            }
        });

        registrationButton.addClickListener((final Button.ClickEvent event) -> {
            final LoginUI loginUI = util.getCurrentUI(LoginUI.class);
            getUI().getPage().setLocation(new ExternalResource(loginUI.getContextPath() + REGISTRATION).getURL());
        });

        fbLoginButton.addClickListener((final Button.ClickEvent event) -> {
            final LoginUI loginUI = util.getCurrentUI(LoginUI.class);
            getUI().getPage().setLocation(new ExternalResource(loginUI.getContextPath() + FB_LOGIN).getURL());
        });

        log.info("button click listeners added");
    }

    private void makeFormContainer() {
        myForm.setMargin(true);
        myForm.setSpacing(true);
        myFormContainer.setCaptionAsHtml(true);
        myForm.addComponents(langChooser.getLangChooser(), usernameField, passwordField, loginButton, registrationButton, fbLoginButton);
        myFormContainer.setContent(myForm);

        log.info("myFormContainer created");
    }

    @Override
    public void updateMessageStrings(final Locale locale) {
        loginButton.setCaption(i18n.get("signin.button.signin"));
        usernameField.setCaption(i18n.get("signin.label.username"));
        passwordField.setCaption(i18n.get("signin.label.password"));
        myFormContainer.setCaption(i18n.get("signin.caption.text"));
        registrationButton.setCaption(i18n.get("signup.label.registration"));
        fbLoginButton.setCaption(i18n.get("signin.button.fb_signin"));

        log.info("localized messages updated to: " + locale);
    }
}
