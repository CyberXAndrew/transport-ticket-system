package com.github.cyberxandrew.service;

import com.github.cyberxandrew.model.User;
import com.github.cyberxandrew.model.UserDetailsImpl;
import com.github.cyberxandrew.repository.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired private UserRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login).orElseThrow(() ->
                new UsernameNotFoundException("User with login: " + login + " not found"));

        UserDetailsImpl userDetails =
                new UserDetailsImpl(user.getId(), user.getLogin(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        return userDetails;
    }
}
