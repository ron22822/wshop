package com.example.wshop.repository;

import com.example.wshop.model.User;

import java.util.Optional;

public interface UserRepositoryCustom {

    public Optional<User> findUserWithOrders(Long id);

    public Optional<User> findUserByUsername(String username);

}
