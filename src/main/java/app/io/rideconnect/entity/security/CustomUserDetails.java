package app.io.rideconnect.entity.security;

import app.io.rideconnect.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements  UserDetails {

    private static final long serialVersionUID = 1L;
    private User user;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;

        this.authorities = this.user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+ role.getName().name()))
                .collect(Collectors.toList());

    }


    public String getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.user.getRoles().stream()
                .map((role)-> new SimpleGrantedAuthority("ROLE_"+ role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement if needed
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}