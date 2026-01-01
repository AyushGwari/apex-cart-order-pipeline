package com.apexcart.identity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    @NotBlank(message = "Username is required")
    private String username;
    @Column(unique = true,nullable = false)
    @Email(message = "Invalid mail format")
    private String email;
    @Column(nullable = false)
    @Size(min = 8,message ="Password must be at least 8 characters")
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),         // Points to THIS entity (User)
            inverseJoinColumns = @JoinColumn(name = "role_id")   // Points to the OTHER entity (Role)
    )    private Set<Role> roles = new HashSet<>();
}
