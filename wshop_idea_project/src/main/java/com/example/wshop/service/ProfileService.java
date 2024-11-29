package com.example.wshop.service;

import com.example.wshop.dto.ProfileDTO;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.Profile;
import com.example.wshop.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository){
        this.profileRepository = profileRepository;
    }

    public ProfileDTO getProfileById(Long id){
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with Id: " + id));
        return mapToDto(profile);
    }

    public Page<ProfileDTO> getAllProfile(int page,int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Profile> profiles = profileRepository.findAll(pageable);
        return profiles.map(this::mapToDto);
    }

    @Transactional
    public ProfileDTO updateProfileById(Long id,ProfileDTO profileDTO){
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with Id: " + id));
        if(profileDTO.getFirstname() != null){
            profile.setFirstname(profileDTO.getFirstname());
        }
        if(profileDTO.getLastname() != null){
            profile.setLastname(profileDTO.getLastname());
        }
        if(profileDTO.getBirthday() != null){
            profile.setBirthday(Date.valueOf(profileDTO.getBirthday()));
        }
        if(profileDTO.getGender() != null){
            profile.setGender(profileDTO.getGender());
        }

        Profile profileUpdate = profileRepository.saveAndFlush(profile);
        return mapToDto(profileUpdate);
    }

    public ProfileDTO mapToDto(Profile profile){
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileid(profile.getProfileid());
        profileDTO.setFirstname(profile.getFirstname());
        profileDTO.setLastname(profile.getLastname());
        if(profile.getBirthday() != null){
            profileDTO.setBirthday(profile.getBirthday().toString());
        }else profileDTO.setBirthday(null);
        profileDTO.setGender(profile.getGender());
        return profileDTO;
    }

}
