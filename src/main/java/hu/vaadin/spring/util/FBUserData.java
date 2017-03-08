package hu.vaadin.spring.util;

import java.util.Map;
import lombok.Getter;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
public class FBUserData {

    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final String email;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    @Getter
    private final String pictureURL;

    public FBUserData(final Map<String, String> fbData) {
        this.id = fbData.get("id");
        this.name = fbData.get("name");
        this.email = fbData.get("email");
        this.firstName = fbData.get("first_name");
        this.lastName = fbData.get("last_name");
        this.pictureURL = fbData.get("picture");
    }
}
