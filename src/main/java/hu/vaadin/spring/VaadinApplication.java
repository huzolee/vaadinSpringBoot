package hu.vaadin.spring;

import hu.vaadin.spring.util.LangChooser;
import hu.vaadin.spring.util.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.vaadin.spring.i18n.MessageProvider;
import org.vaadin.spring.i18n.ResourceBundleMessageProvider;
import org.vaadin.spring.i18n.annotation.EnableI18N;
import org.vaadin.spring.security.annotation.EnableVaadinManagedSecurity;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@gmail.com>
 */
@EnableI18N
@EnableOAuth2Client
@EnableVaadinManagedSecurity
@SpringBootApplication
public class VaadinApplication {

    @Bean
    public MessageProvider messageProvider() {
        return new ResourceBundleMessageProvider("static.i18n.messages");
    }

    @Bean
    public Util utils() {
        return new Util();
    }

    @Bean
    public LangChooser langChooser() {
        return new LangChooser();
    }

    public static void main(final String[] args) {
        SpringApplication.run(VaadinApplication.class, args);
    }
}
