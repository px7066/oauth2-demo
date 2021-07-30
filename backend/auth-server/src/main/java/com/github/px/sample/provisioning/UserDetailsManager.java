package com.github.px.sample.provisioning;

import com.github.px.sample.persistence.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserDetailsManager extends UserDetailsService {
    void createUser(User user);

    void updateUser(User user);

    void deleteUser(String id);

    void changePassword(String id, String password);

    boolean userExists(String id);
}
