package hu.vaadin.spring.dto;

import com.vaadin.data.Item;
import static hu.vaadin.spring.enumeration.Role.ROLE_USER;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@gmail.com>
 */
@Data
public class UserDTO implements Serializable {

    private String name;
    private String email;
    private String confirmEmail;
    private String password;
    private String confirmPassword;
    private String facebookId;
    private String userRole;

    public UserDTO() {
    }

    public UserDTO(final Item item) {
        this.name = item.getItemProperty("name").toString();
        this.email = item.getItemProperty("email").toString();
        this.confirmEmail = item.getItemProperty("confirmEmail").toString();
        this.password = item.getItemProperty("password").toString();
        this.confirmPassword = item.getItemProperty("confirmPassword").toString();
        this.facebookId = item.getItemProperty("facebookId").toString();
        this.userRole = ROLE_USER;
    }
}
