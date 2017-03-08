package hu.vaadin.spring.dto;

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
}
