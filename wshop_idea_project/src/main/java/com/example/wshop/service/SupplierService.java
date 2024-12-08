package com.example.wshop.service;

import com.example.wshop.dto.SupplierDTO;
import com.example.wshop.exception.ResourceNameAlreadyExistsException;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.Supplier;
import com.example.wshop.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public SupplierDTO getById(Long id) {
        return supplierRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with Id: " + id));
    }

    public Page<SupplierDTO> getAllSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        return suppliers.map(this::mapToDto);
    }

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierRepository.findBySuppliername(supplierDTO.getSuppliername()).isPresent()) {
            throw new ResourceNameAlreadyExistsException(
                    "Supplier with supplier name: " + supplierDTO.getSuppliername() + " already exists");
        }
        Supplier supplier = mapToSupplier(supplierDTO);
        supplier = supplierRepository.save(supplier);
        return mapToDto(supplier);
    }

    @Transactional
    public SupplierDTO updateSupplierById(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with Id: " + id));
        Optional<Supplier> supplierOptional = supplierRepository.findBySuppliername(supplierDTO.getSuppliername());
        if(supplierOptional.isPresent()){
            if(!id.equals(supplierOptional.get().getSupplierid())){
                throw new ResourceNameAlreadyExistsException(
                        "Supplier with supplier name: " + supplierDTO.getSuppliername() + " already exists");
            }
        }
        supplier.setSuppliername(supplierDTO.getSuppliername());
        supplier.setContactinfo(supplierDTO.getContactinfo());
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return mapToDto(updatedSupplier);
    }

    @Transactional
    public void deleteSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with Id: " + id));
        supplierRepository.delete(supplier);
    }

    private Supplier mapToSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        supplier.setSuppliername(supplierDTO.getSuppliername());
        supplier.setContactinfo(supplierDTO.getContactinfo());
        return supplier;
    }

    private SupplierDTO mapToDto(Supplier supplier) {
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setSupplierid(supplier.getSupplierid());
        supplierDTO.setSuppliername(supplier.getSuppliername());
        supplierDTO.setContactinfo(supplier.getContactinfo());
        return supplierDTO;
    }
}

