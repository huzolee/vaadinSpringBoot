package hu.vaadin.spring.enumeration;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
public class Path {

    public static final String APPLICATION = "/";
    public static final String LOGIN = "/login";
    public static final String FAILURE = LOGIN + "?error";
    public static final String FB_LOGIN = LOGIN + "/facebook";
    public static final String REGISTRATION = "/registration";
}
