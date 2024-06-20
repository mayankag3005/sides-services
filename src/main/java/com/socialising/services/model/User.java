package com.socialising.services.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.socialising.services.constants.Role;
import com.socialising.services.constants.Status;
import com.socialising.services.model.token.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = { "username", "userId", "phoneNumber" }) })
@Entity
@Data
@Builder
public class User implements UserDetails {

    @Id
    @Column(unique=true)
    private Long userId;

    @Column(unique=true)
    private String username;

    private String password;

    private String email;

    @Column(unique=true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    private String firstName;

    private String lastName;

    private Long userDPId;

    @Enumerated(EnumType.STRING)
    private Status status;              // to check whether is online / offline

    private String dob;

    private Integer age;

    private String gender;

    private String religion;

    private String maritalStatus;

    private String city;

    private String state;

    private String homeCity;

    private String homeState;

    private String country;

    private String education;

    private String occupation;

    private Long[] friendRequests;

    private Long[] friends;

    private Long[] posts;

    private String[] tags;

    private Long[] reminderPosts;

    public void setUserId() {
        this.userId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
