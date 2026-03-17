// package com.billpoint.backend.security;

// import com.billpoint.backend.model.User;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
// import java.util.Collection;
// import java.util.List;

// public class UserDetailsImpl implements UserDetails {
//     private static final long serialVersionUID = 1L;

//     private Long id;
//     private String username;
//     private String email;
//     private String password;
//     private Collection<? extends GrantedAuthority> authorities;
//     private Boolean active;

//     public UserDetailsImpl(Long id, String username, String email, String password, Boolean active,
//                            Collection<? extends GrantedAuthority> authorities) {
//         this.id = id;
//         this.username = username;
//         this.email = email;
//         this.password = password;
//         this.active = active;
//         this.authorities = authorities;
//     }

//     public static UserDetailsImpl build(User user) {
//         List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

//         return new UserDetailsImpl(
//                 user.getId(),
//                 user.getUsername(),
//                 user.getEmail(),
//                 user.getPassword(),
//                 user.getIsActive(),
//                 authorities);
//     }

//     public Long getId() { return id; }
//     public String getEmail() { return email; }

//     @Override
//     public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
//     @Override
//     public String getPassword() { return password; }
//     @Override
//     public String getUsername() { return username; }
//     @Override
//     public boolean isAccountNonExpired() { return true; }
//     @Override
//     public boolean isAccountNonLocked() { return active; }
//     @Override
//     public boolean isCredentialsNonExpired() { return true; }
//     @Override
//     public boolean isEnabled() { return active; }
// }


package com.billpoint.backend.security;

import com.billpoint.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private String password;
    private Boolean active;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Boolean active,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {

        // 🔥 VERY IMPORTANT: always prefix ROLE_
        GrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                Collections.singletonList(authority)
        );
    }

    public Long getId() { return id; }

    public String getEmail() { return email; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return active != null && active;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return active != null && active;
    }
}