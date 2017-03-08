package hu.vaadin.spring.util;

import com.vaadin.spring.annotation.SpringComponent;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.RawAPIResponse;
import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@Slf4j
@SpringComponent
public class FBUser {

    private final OAuth2ClientContext oauthContext;

    private static final String FB_FIELDS = "fields=id,name,email,first_name,last_name,picture";
    private final Map<String, String> fbData = new HashMap<>();
    @Value("${facebook.client.clientId}")
    private String clientId;
    @Value("${facebook.client.clientSecret}")
    private String clientSecret;
    @Value("${facebook.client.scope}")
    private String scope;

    public FBUser(final OAuth2ClientContext oauthContext) {
        this.oauthContext = oauthContext;
    }

    //http://facebook4j.org/en/code-examples.html
    public FBUserData getProfile() {
        fbData.clear();

        if (oauthContext != null) {
            final Facebook facebook = new FacebookFactory().getInstance();
            final String accessToken = oauthContext.getAccessToken().getValue();

            facebook.setOAuthAppId(clientId, clientSecret);

            if (scope != null && !scope.isEmpty()) {
                facebook.setOAuthPermissions(scope);
            }

            facebook.setOAuthAccessToken(new AccessToken(accessToken));

            try {
                final RawAPIResponse resp = facebook.callGetAPI("me?".concat(FB_FIELDS));
                final JSONObject jsonObject = resp.asJSONObject();
                final Iterator keys = jsonObject.keys();

                while (keys.hasNext()) {
                    final String key = keys.next().toString();
                    final String value = jsonObject.get(key).toString();
                    fbData.put(key, value);
                }

                return new FBUserData(fbData);
            } catch (final JSONException | FacebookException ex) {
                log.error(ex.getMessage());
            }
        }

        return null;
    }
}
