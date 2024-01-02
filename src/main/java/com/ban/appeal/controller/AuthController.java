package com.ban.appeal.controller;

import com.ban.appeal.dto.DiscordUser;
import com.ban.appeal.service.TokenService;
import com.ban.appeal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @ExceptionHandler({ ResponseStatusException.class })
    public ResponseEntity<String> handleException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getReason(), e.getStatusCode());
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<String> callback(
        @RequestParam(required = false) String code, @RequestParam(required = false) String error
    ) {
        if (error != null && !error.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discord API returned an error: " + error);
        }

        if (code == null || code.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request param \"code\" is missing");
        }

        DiscordUser user = userService.getUserByToken(userService.getTokenByCode(code));
        String jwt = tokenService.generateDiscordUserToken(user);

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }
}
