package hu.vaadin.spring.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import static com.vaadin.shared.ui.ui.Transport.WEBSOCKET_XHR;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.VerticalLayout;
import static hu.vaadin.spring.enumeration.View.HOME_VIEW_NAME;
import hu.vaadin.spring.util.SessionAttribute;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.i18n.support.TranslatableUI;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@SpringUI()
@Theme("mytheme")
@Push(value = PushMode.AUTOMATIC, transport = WEBSOCKET_XHR)
public class MyVaadinUI extends TranslatableUI {

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private SpringViewProvider viewProvider;

    @Getter
    private Navigator navigator;
    @Getter
    private String contextPath;

    private void setAttributesToVaadinSession() {
        SessionAttribute.SESSION_ATTRIBUTE_LIST.stream().forEach((sa) -> {
            final Object attribute = httpSession.getAttribute(sa.getName());
            if (attribute != null) {
                VaadinSession.getCurrent().setAttribute(sa.getName(), attribute);
            }
        });
    }

    @Override
    protected void initUI(final VaadinRequest vaadinRequest) {
        final VerticalLayout viewContainer = new VerticalLayout();
        final VerticalLayout content = new VerticalLayout();

        contextPath = vaadinRequest.getContextPath();

        viewContainer.addComponent(content);

        navigator = new Navigator(this, content);
        navigator.addProvider(viewProvider);

        setAttributesToVaadinSession();
        setContent(viewContainer);
        navigator.navigateTo(HOME_VIEW_NAME);
    }
}
