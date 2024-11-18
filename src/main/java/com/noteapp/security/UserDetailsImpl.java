package com.noteapp.security;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    /**
     * Constructs a new UserDetailsImpl with the specified properties.
     *
     * @param username The username (email) of the user.
     * @param password The password of the user.
     * @param authorities The authorities (roles) granted to the user.
     * @param accountNonExpired Indicates whether the user's account is non-expired.
     * @param accountNonLocked Indicates whether the user's account is non-locked.
     * @param credentialsNonExpired Indicates whether the user's credentials are non-expired.
     * @param enabled Indicates whether the user is enabled.
     */
    @Builder
    public UserDetailsImpl(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            boolean enabled
    ) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }
}