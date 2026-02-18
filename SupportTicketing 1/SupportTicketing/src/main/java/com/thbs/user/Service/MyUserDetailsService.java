package com.thbs.user.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.thbs.user.Repository.UserRepository;
import com.thbs.user.entity.User;
import com.thbs.user.entity.UserPrincipal;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public MyUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Here 'username' is actually the userName; change to findByEmail if you switch to email login
        User user = repo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserPrincipal(user);
    }
}