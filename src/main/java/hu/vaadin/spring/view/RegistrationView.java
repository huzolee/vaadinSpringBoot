package hu.vaadin.spring.view;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import hu.vaadin.spring.dto.UserDTO;
import static hu.vaadin.spring.enumeration.Role.ROLE_ADMIN;
import static hu.vaadin.spring.enumeration.Role.ROLE_ANONYMOUS;
import static hu.vaadin.spring.enumeration.Role.ROLE_USER;
import static hu.vaadin.spring.enumeration.View.REGISTRATION_VIEW_NAME;
import hu.vaadin.spring.model.User;
import hu.vaadin.spring.service.UserService;
import hu.vaadin.spring.ui.RegistrationUI;
import hu.vaadin.spring.util.FBUserData;
import static hu.vaadin.spring.util.SessionAttribute.USER_DATA_ATTR_NAME;
import hu.vaadin.spring.util.Util;
import java.util.Collection;
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
@Secured(value = {ROLE_ANONYMOUS, ROLE_USER, ROLE_ADMIN})
@SpringView(name = REGISTRATION_VIEW_NAME)
public class RegistrationView extends VerticalLayout implements IAbstractView {

    private final UserService userService;
    private final Util util;
    private final I18N i18n;

    private final FormLayout formLayout = new FormLayout();
    private final Panel registrationPanel = new Panel();
    private final TextField name = new TextField();
    private final PasswordField password = new PasswordField();
    private final PasswordField confirmPassword = new PasswordField();
    private final TextField email = new TextField();
    private final TextField confirmEmail = new TextField();
    private final TextField facebookId = new TextField();
    private final Button registrationButton = new Button();
    private FieldGroup fieldGroup;

    private final AbstractStringValidator passwordCompareValidator = new AbstractStringValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return password.getValue() != null && password.getValue().equals(value);
        }
    };

    private final AbstractStringValidator usernameExistsValidator = new AbstractStringValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return userService.findByUsername(value) == null;
        }
    };

    private final AbstractStringValidator usernameEmptyValidator = new AbstractStringValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return name.getValue() != null && !name.getValue().isEmpty();
        }
    };

    private final AbstractStringValidator passwordEmptyValidator = new StringLengthValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return password.getValue() != null && !password.getValue().isEmpty();
        }
    };

    private final AbstractStringValidator passwordLengthValidator = new StringLengthValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            setMinLength(4);
            return super.isValidValue(value);
        }
    };

    private final AbstractStringValidator confirmPasswordEmptyValidator = new StringLengthValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return confirmPassword.getValue() != null && !confirmPassword.getValue().isEmpty();
        }
    };

    private final AbstractStringValidator emailEmptyValidator = new EmailValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return email.getValue() != null && !email.getValue().isEmpty();
        }
    };

    private final AbstractStringValidator emailFormatValidator = new EmailValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return super.isValidValue(value);
        }
    };

    private final AbstractStringValidator emailExistValidator = new AbstractStringValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return userService.findByEmailAddress(value) == null;
        }
    };

    private final AbstractStringValidator confirmEmailEmptyValidator = new AbstractStringValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return confirmEmail.getValue() != null && !confirmEmail.getValue().isEmpty();
        }
    };

    private final AbstractStringValidator emailCompareValidator = new AbstractStringValidator(null) {

        @Override
        protected boolean isValidValue(final String value) {
            return email.getValue() != null && email.getValue().equals(value);
        }
    };

    public RegistrationView(final UserService userService,
            final Util util, final I18N i18n) {
        this.userService = userService;
        this.util = util;
        this.i18n = i18n;

        addValidators();
        makeFieldGroup();
        addRegistrationButtonClickListener();

        facebookId.setVisible(false);

        final Collection<Field<?>> fields = fieldGroup.getFields();

        util.setNullRepresentationToEmptyStringOnAllAbstractTextField(fields);

        formLayout.addComponents(name, email, confirmEmail, password, confirmPassword,
                registrationButton, facebookId);
        registrationPanel.setContent(formLayout);
        name.focus();

        addComponent(registrationPanel);

        log.info(REGISTRATION_VIEW_NAME + " view created");
    }

    private void addValidators() {
        name.addValidator(usernameExistsValidator);
        name.addValidator(usernameEmptyValidator);
        password.addValidator(passwordEmptyValidator);
        password.addValidator(passwordLengthValidator);
        email.addValidator(emailEmptyValidator);
        email.addValidator(emailFormatValidator);
        email.addValidator(emailExistValidator);
        confirmEmail.addValidator(confirmEmailEmptyValidator);
        confirmEmail.addValidator(emailCompareValidator);
        confirmPassword.addValidator(confirmPasswordEmptyValidator);
        confirmPassword.addValidator(passwordCompareValidator);

        log.info("field validators added");
    }

    private void makeFieldGroup() {
        final UserDTO userDTO = new UserDTO();

        fieldGroup = new BeanFieldGroup<>(UserDTO.class);
        fieldGroup.setItemDataSource(new BeanItem<>(userDTO));
        fieldGroup.bind(name, "name");
        fieldGroup.bind(email, "email");
        fieldGroup.bind(confirmEmail, "confirmEmail");
        fieldGroup.bind(password, "password");
        fieldGroup.bind(confirmPassword, "confirmPassword");
        fieldGroup.bind(facebookId, "facebookId");

        log.info("fieldGroup created");
    }

    private void addRegistrationButtonClickListener() {
        registrationButton.addClickListener((final Button.ClickEvent event) -> {
            try {
                fieldGroup.commit();
                final RegistrationUI registrationUI = util.getCurrentUI(RegistrationUI.class);
                getUI().getPage().setLocation(new ExternalResource(registrationUI.getContextPath()).getURL());
            } catch (final FieldGroup.CommitException ex) {
                log.info("not all the fields filled properly");
                return;
            }

            final Item itemDataSource = fieldGroup.getItemDataSource();
            final UserDTO newUserDTO = new UserDTO(itemDataSource);
            final User savedUser = userService.saveUser(newUserDTO);

            if (newUserDTO.getFacebookId() != null && !newUserDTO.getFacebookId().isEmpty()) {
                VaadinSession.getCurrent().setAttribute(USER_DATA_ATTR_NAME.getName(), savedUser);
            }
        });

        log.info("registration button click listeners added");
    }

    public void setFBUserProfile(final FBUserData fbUserProfile) {
        final String username = fbUserProfile.getName().replace(" ", ".").toLowerCase();
        name.setValue(username);
        email.setValue(fbUserProfile.getEmail());
        confirmEmail.setValue(fbUserProfile.getEmail());
        facebookId.setValue(fbUserProfile.getId());

        log.info("facebook profile setted");
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    @Override
    public void updateMessageStrings(final Locale locale) {
        registrationPanel.setCaption(i18n.get("signup.label.registration"));
        registrationButton.setCaption(i18n.get("signup.button.registration"));

        name.setCaption(i18n.get("signup.label.username"));
        name.setDescription(i18n.get("signup.description.username"));

        email.setCaption(i18n.get("signup.label.email"));
        email.setDescription(i18n.get("signup.description.email"));

        confirmEmail.setCaption(i18n.get("signup.label.confirm_email"));
        confirmEmail.setDescription(i18n.get("signup.description.confirm_email"));

        password.setCaption(i18n.get("signup.label.password"));
        password.setDescription(i18n.get("signup.description.password"));

        confirmPassword.setCaption(i18n.get("signup.label.confirm_password"));
        confirmPassword.setDescription(i18n.get("signup.description.confirm_password"));

        usernameExistsValidator.setErrorMessage(i18n.get("signup.error.username_already_exist"));
        usernameEmptyValidator.setErrorMessage(i18n.get("signup.info.username_required"));

        emailEmptyValidator.setErrorMessage(i18n.get("signup.info.email_address_required"));
        emailFormatValidator.setErrorMessage(i18n.get("signup.error.email_format"));
        emailExistValidator.setErrorMessage(i18n.get("signup.error.email_already_exist"));
        emailCompareValidator.setErrorMessage(i18n.get("signup.error.different_confirmation_email"));

        confirmEmailEmptyValidator.setErrorMessage(i18n.get("signup.info.confirmation_email_address_required"));

        passwordEmptyValidator.setErrorMessage(i18n.get("signup.info.password_required"));
        passwordLengthValidator.setErrorMessage(i18n.get("signup.error.password_min_length"));
        passwordCompareValidator.setErrorMessage(i18n.get("signup.error.different_confirmation_password"));

        confirmPasswordEmptyValidator.setErrorMessage(i18n.get("signup.info.confirm_password_required"));

        log.info("localized messages updated to: " + locale);
    }
}
