package com.example.wshop.service;

import com.example.wshop.constant.StatusEnum;
import com.example.wshop.dto.JwtResponse;
import com.example.wshop.dto.LoginRequest;
import com.example.wshop.dto.UserDTO;
import com.example.wshop.exception.ResourceNameAlreadyExistsException;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.Order;
import com.example.wshop.model.Profile;
import com.example.wshop.model.Role;
import com.example.wshop.model.User;
import com.example.wshop.repository.OrderRepository;
import com.example.wshop.repository.ProfileRepository;
import com.example.wshop.repository.RoleRepository;
import com.example.wshop.repository.UserRepository;
import com.example.wshop.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ProfileRepository profileRepository;
    private final OrderRepository orderRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,ProfileRepository profileRepository,
                       @Lazy JwtTokenProvider jwtTokenProvider, OrderRepository orderRepository, OrderService orderService,
                       @Lazy AuthenticationManager authenticationManager,@Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.profileRepository = profileRepository;
        this.orderRepository = orderRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
    }

    public UserDTO getById(Long id){
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(()  -> new ResourceNotFoundException("User not found with Id: " + id));
    }

    public Page<UserDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToDto);
    }

    public UserDTO getByUsername(String username){
        return userRepository.findUserByUsername(username)
                .map(this::mapToDto)
                .orElseThrow(()  -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public UserDTO updateUserById(Long id,UserDTO userDTO){
        User user = userRepository.findById(id)
                .orElseThrow(()  -> new ResourceNotFoundException("User not found with Id: " + id));
        return updateUser(user,userDTO);
    }

    @Transactional
    public UserDTO updateUser(User user,UserDTO userDTO){
        if(userDTO.getUsername() != null){
            user.setUsername(userDTO.getUsername());
        }
        if(userDTO.getPassword() != null){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if(userDTO.getRole() != null){
            Role role = roleRepository.findByRolename(userDTO.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with rolename: " + userDTO.getRole()));
        }
        if(userDTO.getEmail() != null){
            user.setEmail(userDTO.getEmail());
        }

        User userUpdate = userRepository.saveAndFlush(user);
        return mapToDto(userUpdate);
    }

    @Transactional
    public void deleteUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()  -> new ResourceNotFoundException("User not found with Id: " + id));

        deleteUser(user);
    }

    @Transactional
    public void deleteUser(User user){
        List<Order> orders = orderRepository.findAllUserOrders(user.getUserid());
        for(Order order : orders){
            if(StatusEnum.CREATED.getString().equals(order.getStatus())){
                orderService.setOrderStatusCancelled(order.getOrderid(),user);
            }
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO){
        if(userRepository.findUserByUsername(userDTO.getUsername()).isPresent()){
            throw new ResourceNameAlreadyExistsException("User with username: "+userDTO.getUsername()+" already exists");
        }
        User user = mapToUser(userDTO);
        user = userRepository.saveAndFlush(user);
        Profile profile = new Profile();
        profile.setProfileid(user.getUserid());
        profile.setUser(user);
        profileRepository.saveAndFlush(profile);
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

    public UserDTO mapToDto(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserid(user.getUserid());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole().getRolename());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    public User mapToUser(UserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findByRolename(userDTO.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with rolename: " + userDTO.getRole()));
        user.setRole(role);
        user.setEmail(userDTO.getEmail());
        return user;
    }
}


