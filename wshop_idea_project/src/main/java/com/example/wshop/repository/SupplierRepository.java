package com.example.wshop.repository;

import com.example.wshop.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Long> {

    public Optional<Supplier> findBySuppliername(String suppliername);

}
