package hu.vaadin.spring.util;

import com.vaadin.server.VaadinSession;
import hu.vaadin.spring.model.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 * @param <T>
 */
public class SessionAttribute<T> implements Serializable {

    public static final List<SessionAttribute> SESSION_ATTRIBUTE_LIST = new ArrayList<>();
    public static final SessionAttribute<User> USER_DATA_ATTR_NAME = new SessionAttribute<>("userData", User.class);
    public static final SessionAttribute<FBUserData> FB_DATA_ATTR_NAME = new SessionAttribute<>("fbData", FBUserData.class);

    @Getter
    private final String name;
    @Getter
    private final Class<T> type;

    private SessionAttribute(final String name, final Class<T> type) {
        this.name = name;
        this.type = type;
        SessionAttribute.SESSION_ATTRIBUTE_LIST.add(this);
    }

    public static <T> T get(final SessionAttribute<? extends T> attribute) {
        final Class<? extends T> c = attribute.type;
        return c.cast(VaadinSession.getCurrent().getAttribute(attribute.name));
    }
}
