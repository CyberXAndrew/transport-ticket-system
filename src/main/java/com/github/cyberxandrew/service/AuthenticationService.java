//package com.github.cyberxandrew.service;
//
//import com.github.cyberxandrew.model.User;
//import com.github.cyberxandrew.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthenticationService {
//    @Autowired private UserRepository userRepository;
//    @Autowired private PasswordEncoder passwordEncoder;
//
//    public User register(String login, String password, String fullName) {
//        if (userRepository.existsByLogin(login)) throw new IllegalArgumentException("Login is already occupied");
//        User user = new User();
//        user.setLogin(login);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setFullName(fullName);
//        return userRepository.save(user);
//    }
//
//    public User authenticate(String login, String password) {
//        User user = userRepository.findByLogin(login).orElseThrow(() -> new IllegalArgumentException("Invalid login"));
//        if (!passwordEncoder.matches(password, user.getPassword()) throw new IllegalArgumentException("Invalid login");
//        return user;
//    }
//}
