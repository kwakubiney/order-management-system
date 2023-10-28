package com.example.ordermanagementsystem.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "users")
    private List<Order> orders;

    public enum Role implements GrantedAuthority {
        NORMAL,
        ADMIN;
        @Override
        public String getAuthority() {
            return "ROLE_" + this.name();
        }
    }
}