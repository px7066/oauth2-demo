package com.github.px.sample.provisioning;

import com.github.px.sample.persistence.User;
import com.github.px.sample.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


@Service
@Slf4j
public class JPAUserManager implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A);


    @Override
    public void createUser(User user) {
        if(user != null){
            if(user.getPassword() != null){
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(user);
        }
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        if(!ObjectUtils.isEmpty(id)){
            userRepository.deleteById(id);
        }
    }

    @Override
    public void changePassword(String id, String password) {
        if(!ObjectUtils.isEmpty(id)){
            userRepository.findById(id).ifPresent(user -> user.setPassword(password));
        }
    }

    @Override
    public boolean userExists(String id) {
        if(!ObjectUtils.isEmpty(id)){
            return userRepository.findById(id).isPresent();
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!ObjectUtils.isEmpty(username)){
            User user = userRepository.findByUsername(username);
            return user;
        }
        return null;
    }
}
