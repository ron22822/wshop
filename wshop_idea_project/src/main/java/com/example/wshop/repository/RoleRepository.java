package com.example.wshop.repository;

import com.example.wshop.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findByRolename(String rolename);

}

