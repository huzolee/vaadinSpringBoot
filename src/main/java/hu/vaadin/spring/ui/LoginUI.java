package hu.vaadin.spring.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import static hu.vaadin.spring.enumeration.Path.LOGIN;
import hu.vaadin.spring.view.LoginView;
import hu.vaadin.spring.util.Util;
import java.util.Locale;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.i18n.support.TranslatableUI;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@SpringUI(path = LOGIN)
@Theme("mytheme")
public class LoginUI extends TranslatableUI {

    @Autowired
    private LoginView loginView;
    @Autowired
    private Util util;
    @Getter
    private String contextPath;

    public void changeLoginLayoutLang(final Locale locale) {
        loginView.updateMessageStrings(locale);
    }

    @Override
    protected void initUI(final VaadinRequest vaadinRequest) {
        contextPath = vaadinRequest.getContextPath();

        if (!util.isAnonymous()) {
            getUI().getPage().setLocation(new ExternalResource(contextPath).getURL());
            return;
        }

        setContent(loginView);
    }
}
