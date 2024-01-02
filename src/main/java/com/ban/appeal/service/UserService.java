package com.ban.appeal.service;

import com.ban.appeal.dto.DiscordTokenResponse;
import com.ban.appeal.dto.DiscordUser;
import com.ban.appeal.util.URIBuilder;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret}")
    private String clientSecret;

    private final Map<String, String> defaultParams = new HashMap<>();

    private Map<String, String> getDefaultParams() {
        if (defaultParams.isEmpty()) {
            defaultParams.put("client_id", clientId);
            defaultParams.put("client_secret", clientSecret);
            defaultParams.put("grant_type", "authorization_code");
            defaultParams.put("scope", "identify");
        }

        return defaultParams;
    }

    public String getTokenByCode(String code) {
        try {
            URI uri = new URIBuilder()
                .uri("https://discord.com/api/v10/oauth2/token")
                .addParams(getDefaultParams())
                .addParam("redirect_uri", "http://localhost:8080/oauth/callback")
                .addParam("code", code)
                .build();

            System.out.println(uri);

            HttpRequest tokenRequest = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(uri.getQuery()))
                .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(tokenResponse.body());

            if (tokenResponse.statusCode() >= 400) {
                HttpStatus status = HttpStatus.resolve(tokenResponse.statusCode());
                throw new ResponseStatusException(
                    status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR,
                    tokenResponse.body()
                );
            }

            DiscordTokenResponse responseJson = new Gson().fromJson(tokenResponse.body(), DiscordTokenResponse.class);

            return responseJson.token_type() + " " + responseJson.access_token();
        } catch (Exception e) {
            throw new RuntimeException("Unable to get user token", e);
        }
    }

    public DiscordUser getUserByToken(String token) {
        try {
            HttpRequest userRequest = HttpRequest
                .newBuilder()
                .uri(new URI("https://discord.com/api/users/@me"))
                .header("Authorization", token)
                .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> userResponse = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
            if (userResponse.statusCode() >= 400) {
                HttpStatus status = HttpStatus.resolve(userResponse.statusCode());
                throw new ResponseStatusException(
                    status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR,
                    userResponse.body()
                );
            }

            return new Gson().fromJson(userResponse.body(), DiscordUser.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get user by its token", e);
        }
    }
}
