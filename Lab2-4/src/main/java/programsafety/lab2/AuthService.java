package programsafety.lab2;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AllArgsConstructor;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;


@Service
@AllArgsConstructor
public class AuthService {

    private Environment env;

    public HttpResponse<JsonNode> logIn(String username, String pass) {
        String body = String.format("grant_type=password&" +
                "username=%s&" +
                "password=%s&" +
                "scope=offline_access&" +
                "client_id=%s&" +
                "client_secret=%s&" +
                "audience=%s", username, pass,
                env.getProperty("okta.oauth2.client-id"),
                env.getProperty("okta.oauth2.client-secret"),
                env.getProperty("okta.oauth2.audience"));

        try {
            return Unirest.post("https://dev-l5yejihpj316wdj1.us.auth0.com/oauth/token")
                    .header("content-type", "application/x-www-form-urlencoded")
                    .body(body)
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<JsonNode> signUp(String username, String pass) {
        String access_token = getAppAccessToken();
        String body = String.format("connection=Username-Password-Authentication&" +
                        "email=%s&" + "password=%s&" + "username=%s",
                        username, pass, username.split("@")[0]);
        System.out.println(body);
        try {
            return Unirest.post("https://dev-l5yejihpj316wdj1.us.auth0.com/api/v2/users")
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("Authorization", String.format("Bearer %s", access_token))
                    .body(body)
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }



    private String getAppAccessToken() {
        String body = String.format("grant_type=client_credentials&" +
                        "client_id=%s&" +
                        "client_secret=%s&" +
                        "audience=%s",
                env.getProperty("okta.oauth2.client-id"), env.getProperty("okta.oauth2.client-secret"), env.getProperty("okta.oauth2.audience"));

        try {
            return Unirest.post("https://dev-l5yejihpj316wdj1.us.auth0.com/oauth/token")
                    .header("content-type", "application/x-www-form-urlencoded")
                    .body(body)
                    .asJson()
                    .getBody().getObject().get("access_token").toString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<JsonNode> updateTokenWithRefresh(String refresh_token) {
        String body = String.format("grant_type=refresh_token&" +
                        "refresh_token=%s&" +
                        "client_id=%s&" +
                        "client_secret=%s&",
                refresh_token,
                env.getProperty("okta.oauth2.client-id"),
                env.getProperty("okta.oauth2.client-secret"));

        try {
            return Unirest.post("https://dev-l5yejihpj316wdj1.us.auth0.com/oauth/token")
                    .header("content-type", "application/x-www-form-urlencoded")
                    .body(body)
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public void addParamsToSession(Map session, String access_token, String refresh_token, String username) {
        session.put("access_token", access_token);
        session.put("header", getHeaderFromJWT(access_token));
        session.put("payload", getPayloadFromJWT(access_token, session));
        session.put("refresh_token", refresh_token);
        session.put("username", username);
        session.put("message", "");
    }

    public void updateAccessTokenInSession(Map session, String access_token) {
        session.put("access_token", access_token);
        session.put("header", getHeaderFromJWT(access_token));
        session.put("payload", getPayloadFromJWT(access_token, session));
    }

    public String getHeaderFromJWT(String jwt) {
        String codeHeader = jwt.split("\\.")[0];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String decodeHeader = new String(decoder.decode(codeHeader));
        return decodeHeader;
    }

    public String getPayloadFromJWT(String jwt, Map session) {
        String codePayload = jwt.split("\\.")[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String decodePayload = new String(decoder.decode(codePayload));

        Map payload = JsonParserFactory.getJsonParser().parseMap(decodePayload);
        session.put("expirationTime", payload.get("exp").toString());

        return decodePayload;
    }
}
