package com.noteapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noteapp.entity.auth.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Check;

import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Check(constraints = "email LIKE '%@%'")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;

    /**
     * Constructs a new User with the specified email and roles.
     *
     * @param email The email of the user.
     * @param roles The roles assigned to the user.
     */
    public User(String email, Set<Role> roles) {
        this.email = email;
        this.roles = roles;
    }

    public User(String email, String password, Set<Role> roles){
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
