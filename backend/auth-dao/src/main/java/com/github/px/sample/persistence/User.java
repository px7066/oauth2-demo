package com.github.px.sample.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;

@Entity
@Table(name = "user")
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"password", "createTime", "updateTime"})
public class User extends BaseEntity implements UserDetails, CredentialsContainer {

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "accountNonExpired")
    private Boolean accountNonExpired;

    @Column(name = "accountNonLocked")
    private Boolean accountNonLocked;

    @Column(name = "credentialsNonExpired")
    private Boolean credentialsNonExpired;

    @Column(name = "enable")
    private Boolean enable;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
