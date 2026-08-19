package com.webgis.evaluatorprofile;

import com.webgis.MessageDto;
import com.webgis.evaluatorprofile.dto.ResponseEvaluatorProfileDto;
import com.webgis.evaluatorprofile.dto.SaveEvaluatorProfileDto;
import com.webgis.security.CookieService;
import com.webgis.security.JwtService;
import com.webgis.user.User;
import com.webgis.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/evaluatorProfile")
public class EvaluatorProfileController {

    private final EvaluatorProfileService evaluatorProfileService;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final UserService userService;

    public EvaluatorProfileController(EvaluatorProfileService evaluatorProfileService, JwtService jwtService, CookieService cookieService, UserService userService) {
        this.evaluatorProfileService = evaluatorProfileService;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.userService = userService;
    }

    @PostMapping("/save")
    public ResponseEntity<Object> saveProfile(
            @RequestBody @Valid SaveEvaluatorProfileDto saveEvaluatorProfileDto,
            HttpServletRequest request) {
        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("You are not logged in or your cookie is not valid"));
        }

        final User user = optionalUser.get();

        try {
            final EvaluatorProfile evaluatorProfile = evaluatorProfileService.saveProfile(saveEvaluatorProfileDto, user);
            final ResponseEvaluatorProfileDto responseEvaluatorProfileDto = new ResponseEvaluatorProfileDto(evaluatorProfile);
            return ResponseEntity.status(200).body(responseEvaluatorProfileDto);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(new MessageDto(e.getMessage()));
        }
    }


    @GetMapping("/hasProfile")
    public ResponseEntity<Object> hasProfile(HttpServletRequest request) {
        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("You are not logged in or your cookie is not valid"));
        }

        final User user = optionalUser.get();
        final boolean hasProfile = evaluatorProfileService.hasProfile(user);
        return ResponseEntity.status(200).body(hasProfile);
    }

    @GetMapping("/getProfile")
    public ResponseEntity<Object> getProfile(HttpServletRequest request) {
        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("You are not logged in or your cookie is not valid"));
        }

        final User user = optionalUser.get();
        final Optional<EvaluatorProfile> optionalEvaluatorProfile = evaluatorProfileService.getProfileForUser(user);

        if (optionalEvaluatorProfile.isEmpty()) {
            return ResponseEntity.status(404).body(new MessageDto("No evaluator profile found for this user"));
        }

        final ResponseEvaluatorProfileDto responseEvaluatorProfileDto = new ResponseEvaluatorProfileDto(optionalEvaluatorProfile.get());
        return ResponseEntity.status(200).body(responseEvaluatorProfileDto);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateProfile(
            @RequestBody @Valid SaveEvaluatorProfileDto saveEvaluatorProfileDto,
            HttpServletRequest request) {
        final String token = cookieService.getJwtFromCookie(request);
        final String username = jwtService.extractUsername(token);
        final Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageDto("You are not logged in or your cookie is not valid"));
        }

        final User user = optionalUser.get();

        try {
            final EvaluatorProfile evaluatorProfile = evaluatorProfileService.updateProfile(saveEvaluatorProfileDto, user);
            final ResponseEvaluatorProfileDto responseEvaluatorProfileDto = new ResponseEvaluatorProfileDto(evaluatorProfile);
            return ResponseEntity.status(200).body(responseEvaluatorProfileDto);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(new MessageDto(e.getMessage()));
        }
    }
}
