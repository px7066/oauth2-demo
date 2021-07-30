package com.github.px.sample.init;

import com.github.px.sample.persistence.User;
import com.github.px.sample.provisioning.JPAUserManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserInit implements CommandLineRunner {

    @Autowired
    private JPAUserManager UserManager;

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEnable(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        UserDetails userDetails = UserManager.loadUserByUsername("user");
        if(userDetails == null){
            UserManager.createUser(user);
        }
    }
}
