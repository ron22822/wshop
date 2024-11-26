package com.example.wshop.service;

import com.example.wshop.dto.JwtResponse;
import com.example.wshop.dto.LoginRequest;
import com.example.wshop.dto.UserDTO;
import com.example.wshop.model.Role;
import com.example.wshop.model.User;
import com.example.wshop.repository.RoleRepository;
import com.example.wshop.repository.UserRepository;
import com.example.wshop.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       @Lazy JwtTokenProvider jwtTokenProvider,
                       @Lazy AuthenticationManager authenticationManager,@Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getByUsername(String username){
        return userRepository.findUserByUsername(username)
                .map(this::mapToDto)
                .orElseThrow(()  -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO){
        if(userRepository.findUserByUsername(userDTO.getUsername()).isPresent()){
            throw new RuntimeException("User with that username already exists");
        }
        User user = mapToUser(userDTO);
        user = userRepository.saveAndFlush(user);
        return mapToDto(user);
    }

    public JwtResponse authUser(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = loadUserByUsername(request.getUsername());

        var jwt = jwtTokenProvider.generateToken(user);
        return new JwtResponse(jwt);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user)
                )).orElseThrow(()  -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRolename()));
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByUsername(username).get();
    }

    private UserDTO mapToDto(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserid(user.getUserid());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole().getRolename());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private User mapToUser(UserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findByRolename(userDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found with rolename: " + userDTO.getRole()));
        user.setRole(role);
        user.setEmail(userDTO.getEmail());
        return user;
    }
}


