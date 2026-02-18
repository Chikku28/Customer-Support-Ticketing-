package com.thbs.user.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.thbs.user.Service.JWTService;
import com.thbs.user.Service.UserService;
import com.thbs.user.entity.Ticket;
import com.thbs.user.entity.User;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JWTService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    private final String TICKET_SERVICE_BASE = "http://TICKETRISING/Tickets";
    
    @Autowired
    private RestTemplate restTemplate;
    
    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        Ticket[] arr = restTemplate.getForObject(TICKET_SERVICE_BASE, Ticket[].class);
        return (arr == null) ? List.of() : Arrays.asList(arr);
    }

    @GetMapping("/ticket/tickets/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return restTemplate.getForObject(TICKET_SERVICE_BASE + "/" + id, Ticket.class);
    }



    @PostMapping("/user/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        String rawPassword = user.getPassword();

        User saved = userService.createUser(user);

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(saved.getUserName(), rawPassword)
        );

        String token = jwtService.generateToken(auth);

        return ResponseEntity.ok(Map.of(
            "message", "Registration successful",
            "token", token,
            "userId", saved.getId(),
            "userName", saved.getUserName(),
            "role", saved.getRole().name()
        ));
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String userName = body.get("userName"); 
        String password = body.get("password");

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userName, password)
        );

        String token = jwtService.generateToken(auth);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "userName", userName
        ));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
