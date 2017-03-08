package hu.vaadin.spring.repository;

import hu.vaadin.spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@gmail.com>
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByName(final String name);

    public User findByEmail(final String email);

    public User findByFacebookId(final String facebookId);
}
