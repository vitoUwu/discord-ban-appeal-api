package com.ban.appeal.controller;

import com.ban.appeal.dto.DiscordUser;
import com.ban.appeal.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class UserController {

    @Autowired
    private TokenService tokenService;

    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<String> handleException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getReason(), e.getStatusCode());
    }

    @ExceptionHandler({MissingRequestCookieException.class})
    public ResponseEntity<String> handleMissingCookieException(MissingRequestCookieException e) {
        return new ResponseEntity<>("Required token cookie is missing", e.getStatusCode());
    }

    @GetMapping("/me")
    public ResponseEntity<DiscordUser> getUser(@CookieValue("dt") String token) {
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token cookie is missing");
        }

        try {
            return new ResponseEntity<>(tokenService.validateToken(token), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
