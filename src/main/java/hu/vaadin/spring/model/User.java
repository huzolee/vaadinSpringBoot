package hu.vaadin.spring.model;

import hu.vaadin.spring.dto.UserDTO;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@gmail.com>
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Getter
    @NotNull
    @Column
    private String name;
    @Getter
    @NotNull
    @Column
    private String email;
    @Getter
    @Setter
    @NotNull
    @Column
    private String password;
    @Lob
    @Getter
    @Column
    private String facebookId;
    @Getter
    private String userRole;

    public User() {
    }

    public User(final String name, final String email,
            final String password, final String userRole) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public User(final String name, final String email,
            final String password, final String userRole, final String facebookId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.facebookId = facebookId;
    }

    public User(final UserDTO userDTO) {
        name = userDTO.getName();
        email = userDTO.getEmail();
        password = userDTO.getPassword();
        userRole = userDTO.getUserRole();

        final String fbId = userDTO.getFacebookId();

        if (fbId != null) {
            facebookId = fbId;
        }
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", email=" + email + ","
                + " password=[PROTECTED] , userRole=" + userRole + " ,facebookId=" + facebookId + '}';
    }
}
