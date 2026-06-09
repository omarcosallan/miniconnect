package dev.marcos.miniconnect.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.marcos.miniconnect.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private UUID id;
    private String name;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    @Override
    public String getUsername() {
        return email;
    }
}
