package com.example.wshop.controllers;

import com.example.wshop.dto.ProfileDTO;
import com.example.wshop.model.User;
import com.example.wshop.service.ProfileService;
import com.example.wshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable Long id){
        ProfileDTO profileDTO = profileService.getProfileById(id);
        return ResponseEntity.ok(profileDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> getCurrentProfile(){
        User user = userService.getCurrentUser();
        ProfileDTO profileDTO = profileService.getProfileById(user.getUserid());
        return ResponseEntity.ok(profileDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProfileDTO>> getAllProfile(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<ProfileDTO> profiles = profileService.getAllProfile(page,size);
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileDTO> updateProfileById(@PathVariable Long id,@RequestBody ProfileDTO profileDTO){
        ProfileDTO profileDtoUpdate = profileService.updateProfileById(id,profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileDtoUpdate);
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileDTO> updateCurrentProfile(@RequestBody ProfileDTO profileDTO){
        User user = userService.getCurrentUser();
        ProfileDTO profileDtoUpdate = profileService.updateProfileById(user.getUserid(),profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileDtoUpdate);
    }

}
