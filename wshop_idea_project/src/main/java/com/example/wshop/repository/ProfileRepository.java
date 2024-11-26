package com.example.wshop.repository;

import com.example.wshop.model.Profile;
import com.example.wshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {

    public Optional<Profile> findByUser(User user);

}
