package com.example.wshop.controllers;

import com.example.wshop.dto.UserDTO;
import com.example.wshop.model.User;
import com.example.wshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(userService.mapToDto(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable Long id,@RequestBody UserDTO userDTO){
        UserDTO userDtoUpdate = userService.updateUserById(id,userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtoUpdate);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserDTO userDTO){
        User user = userService.getCurrentUser();
        UserDTO userDtoUpdate = userService.updateUser(user,userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtoUpdate);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted User id: "+id);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser(){
        User user = userService.getCurrentUser();
        userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Your user successfully deleted");
    }


}
