package com.example.wshop.repository;

import com.example.wshop.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<User> findUserWithOrders(Long id) {
        try {
            User user = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.orders " +
                    "WHERE u.userid = :userID",User.class)
                    .setParameter("userID",id)
                    .getSingleResult();
            return Optional.of(user);
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        try {
            User user = entityManager.createQuery("SELECT u FROM User u " +
                            "WHERE u.username = :Username",User.class)
                    .setParameter("Username",username)
                    .getSingleResult();
            return Optional.of(user);
        }catch (NoResultException e){
            return Optional.empty();
        }
    }
}
