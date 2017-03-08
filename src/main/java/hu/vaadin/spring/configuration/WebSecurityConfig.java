package hu.vaadin.spring.configuration;

import static hu.vaadin.spring.enumeration.Path.APPLICATION;
import static hu.vaadin.spring.enumeration.Path.FAILURE;
import static hu.vaadin.spring.enumeration.Path.FB_LOGIN;
import static hu.vaadin.spring.enumeration.Path.LOGIN;
import hu.vaadin.spring.service.UserService;
import javax.servlet.Filter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@gmail.com>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ENCODING = "UTF-8";

    private final OAuth2ClientContext oauth2ClientContext;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(final UserService userService, final PasswordEncoder passwordEncoder) {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConfigurationProperties("facebook.client")
    public OAuth2ProtectedResourceDetails facebookResourceDetails() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("facebook.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(final OAuth2ClientContextFilter filter) {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    public WebSecurityConfig(final OAuth2ClientContext oauth2ClientContext, final ApplicationEventPublisher applicationEventPublisher) {
        this.oauth2ClientContext = oauth2ClientContext;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private CharacterEncodingFilter getCharacterEncodingFilter() {
        final CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding(ENCODING);
        filter.setForceEncoding(true);
        return filter;
    }

    private Filter getSSOFilter() {
        final OAuth2ClientAuthenticationProcessingFilter facebookFilter = new OAuth2ClientAuthenticationProcessingFilter(FB_LOGIN);
        final OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(facebookResourceDetails(), oauth2ClientContext);
        facebookFilter.setApplicationEventPublisher(applicationEventPublisher);
        facebookFilter.setRestTemplate(facebookTemplate);
        String userInfoUri = facebookResource().getUserInfoUri();
        String clientId = facebookResourceDetails().getClientId();
        facebookFilter.setTokenServices(new UserInfoTokenServices(userInfoUri, clientId));
        return facebookFilter;
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/VAADIN/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/flags/**", "/login", "/registration", "/vaadinServlet/UIDL/**",
                        "/vaadinServlet/HEARTBEAT/**", "/vaadinServlet/PUSH/**",
                        "/vaadinServlet/popup/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl(APPLICATION)
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN))
                .accessDeniedPage(FAILURE)
                .and()
                .addFilterBefore(getCharacterEncodingFilter(), CsrfFilter.class)
                .addFilterBefore(getSSOFilter(), BasicAuthenticationFilter.class);
    }
}
