package com.data.covid.controllers;


import com.data.covid.payload.request.LoginRequest;
import com.data.covid.payload.request.SignupRequest;
import com.data.covid.payload.response.JwtResponse;
import com.data.covid.payload.response.MessageResponse;

import com.data.covid.repositories.RoleRepository;
import com.data.covid.repositories.UserRepository;
import com.data.covid.security.ERole;
import com.data.covid.security.Role;
import com.data.covid.security.User;
import com.data.covid.exceptions.NotFoundException;


import com.data.covid.security.jwt.JwtUtils;
import com.data.covid.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    AuthenticationManager authenticationManager;
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder encoder;
    JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = new BCryptPasswordEncoder(11);
        this.jwtUtils = jwtUtils;
    }

    // login

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    // Register

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody  SignupRequest signupRequest) {

        if(!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Passwords do not match!"));
        }

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }

    // return all users

    @GetMapping(value={"/","/list"} )
    @PreAuthorize(" hasRole('ROLE_ADMIN')")
    public @ResponseBody List<User> list(){
        return userRepository.findAll();
    }

    // return user by id

    @GetMapping(value="/user/findById")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody User findById(@RequestParam("id") Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Error: User Id : %d is not found.",id)));
    }

    // return user by firstname

    @GetMapping(value="/user/findByUsername/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody User findByUsername(@RequestParam("username") String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: Username : ["+username+"] is not found."));
    }

    // delete by id

    @DeleteMapping(value="/user/deleteById")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteById(@RequestParam("id") Long id){

        logger.info("delete User by id = "+id);
        if(!userRepository.existsById(id))
            return ResponseEntity.ok(new MessageResponse("Error: Cannot delete User with id: "+id));

        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("User deleted with id: " + id));
    }

    // edit/update a user record - only if record with id exists

    @PostMapping(value="/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody ResponseEntity<MessageResponse> update(@ModelAttribute User user){
        Long id = user.getId();

        // check if exists first
        // then update

        if(!userRepository.existsById(id))
            ResponseEntity.ok(new MessageResponse("Error: Id does not exist ["+id+"] -> cannot update record"));

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User record updated"));
    }

    @GetMapping(value="/checkToken" )
//    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public @ResponseBody boolean checkToken(@RequestParam("token") String token){
        return jwtUtils.validateJwtToken(token);
    }

}