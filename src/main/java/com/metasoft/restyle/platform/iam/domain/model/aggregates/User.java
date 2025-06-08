package com.metasoft.restyle.platform.iam.domain.model.aggregates;

import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * User aggregate root
 * This class represents the aggregate root for the User entity.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // Eager fetch to load the roles when loading the user
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private String email;
    private String firstName;
    private String paternalSurname;
    private String maternalSurname;
    private String description;
    private String phone;
    private String image;

    public User() {
        this.roles = new HashSet<>();
    }
    public User(String username, String password, String email, String firstName, String paternalSurname, String maternalSurname, String description, String phone, String image) {
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
        this.email = email;
        this.firstName = firstName;
        this.paternalSurname = paternalSurname;
        this.maternalSurname = maternalSurname;
        this.description = description;
        this.phone = phone;
        this.image = image;
    }

    public User(String username, String password, List<Role> roles, String email, String firstName, String paternalSurname, String maternalSurname, String description, String phone, String image) {
        this(username, password, email, firstName, paternalSurname, maternalSurname, description, phone, image);
        addRoles(roles);
    }

    public void updateUserInfo(String email, String description, String phone, String image) {
        this.email = email;
        this.description = description;
        this.phone = phone;
        this.image = image;
    }

    /**
     * Add a role to the user
     * @param role the role to add
     * @return the user with the added role
     */
    public User addRole(Role role) {
        if(role == null || role.getId() == null) {
            this.roles.add(Role.getDefaultRole());
        } else {
            this.roles.add(role);
        }
        return this;
    }

    /**
     * Add a list of roles to the user
     * @param roles the list of roles to add
     * @return the user with the added roles
     */
    public User addRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            this.roles.add(Role.getDefaultRole());
        } else {
            this.roles.addAll(roles);
        }
        return this;
    }

    public Long getId() {
        return super.getId();
    }

}
