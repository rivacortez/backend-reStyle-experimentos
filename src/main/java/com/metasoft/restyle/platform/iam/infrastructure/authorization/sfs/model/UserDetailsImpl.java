package com.metasoft.restyle.platform.iam.infrastructure.authorization.sfs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class is responsible for providing the user details to the Spring Security framework.
 * It implements the UserDetails interface.
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {

    private final String username;
    @JsonIgnore
    private final String password;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    /**
     * This method is responsible for building the UserDetailsImpl object from the User object.
     * @param user The user object.
     * @return The UserDetailsImpl object.
     */
    public static UserDetailsImpl build(User user) {
        var authorities = user.getRoles().stream()
                .map(role -> role.getName().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    // Los siguientes métodos deben implementarse manualmente para cumplir con la interfaz UserDetails de Spring Security.
    // Lombok no puede generar los métodos con el prefijo exacto 'is' requerido por la interfaz, por lo que es necesario mantenerlos manualmente.
    // Suprimimos la advertencia de código redundante ya que esto es intencional y necesario para la compatibilidad.

    @SuppressWarnings("all")
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @SuppressWarnings("all")
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @SuppressWarnings("all")
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @SuppressWarnings("all")
    public boolean isEnabled() {
        return enabled;
    }

    @SuppressWarnings("all")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
