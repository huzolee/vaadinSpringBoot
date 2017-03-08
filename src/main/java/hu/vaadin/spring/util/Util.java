package hu.vaadin.spring.util;

import com.vaadin.navigator.Navigator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;
import static hu.vaadin.spring.enumeration.Role.ROLE_ANONYMOUS;
import hu.vaadin.spring.ui.LoginUI;
import hu.vaadin.spring.ui.MyVaadinUI;
import hu.vaadin.spring.ui.RegistrationUI;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@SpringComponent
public class Util {

    public boolean isAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        final Iterator<? extends GrantedAuthority> auths = authorities.iterator();
        boolean anon = true;

        while (auths.hasNext()) {
            final GrantedAuthority auth = auths.next();
            anon = ROLE_ANONYMOUS.equals(auth.getAuthority());
        }

        return anon;
    }

    public void setNullRepresentationToEmptyStringOnAllAbstractTextField(final Collection<Field<?>> fields) {
        fields.stream().filter((f) -> (f instanceof AbstractTextField)).forEach((f) -> {
            AbstractTextField.class.cast(f).setNullRepresentation(new String());
        });
    }

    public <T extends UI> T getCurrentUI(final Class<T> klass) {
        final UI currentUI = UI.getCurrent();

        if (currentUI != null) {
            if (currentUI instanceof MyVaadinUI) {
                return klass.cast(currentUI);
            } else if (currentUI instanceof LoginUI) {
                return klass.cast(currentUI);
            } else if (currentUI instanceof RegistrationUI) {
                return klass.cast(currentUI);
            }
        }

        return null;
    }

    public void changeView(final String viewName) {
        final UI ui = getCurrentUI(MyVaadinUI.class);
        final Navigator navigator = ui.getNavigator();
        navigator.navigateTo(viewName);
    }
}
