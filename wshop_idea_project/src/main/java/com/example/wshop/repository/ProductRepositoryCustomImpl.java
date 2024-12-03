package com.example.wshop.repository;

import com.example.wshop.constant.ActivityEnum;
import com.example.wshop.dto.ProductFilter;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;



    @Override
    public List<Product> findAllActiveProductsWithCategoryAndSupplier() {
        String query = "SELECT p "+
                       "FROM Product p "+
                       "LEFT JOIN FETCH p.category " +
                       "LEFT JOIN FETCH p.supplier " +
                       "WHERE p.activity = :active";
        return entityManager.createQuery(query, Product.class)
                .setParameter("active", ActivityEnum.ACTIVE.getString())
                .getResultList();
    }

    @Override
    public Optional<Product> findProductWithCategoryAndSupplier(Long id) {
        try{
            Product product = entityManager.createQuery(
                "SELECT p FROM Product p "+
                "LEFT JOIN FETCH p.category "+
                "LEFT JOIN FETCH p.supplier "+
                "WHERE p.productid = :productID",Product.class)
                .setParameter("productID",id)
                .getSingleResult();
            return Optional.of(product);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    public List<Product> findAllProductsWithCategoryAndSupplier() {
        String query = "SELECT p FROM Product p " +
                "LEFT JOIN FETCH p.category " +
                "LEFT JOIN FETCH p.supplier";
        return entityManager.createQuery(query, Product.class).getResultList();
    }

    @Override
    public List<Product> findByFilter(ProductFilter filter,Boolean active) {
        StringBuilder queryBuilder = new StringBuilder("SELECT p FROM Product p WHERE 1=1");

        if(active){
            queryBuilder.append(" AND p.activity = :active");
        }

        if (filter.getMinQuantity() != null) {
            queryBuilder.append(" AND p.totalquantity >= :minQuantity");
        }
        if (filter.getMaxQuantity() != null) {
            queryBuilder.append(" AND p.totalquantity <= :maxQuantity");
        }

        if (filter.getMinPrice() != null) {
            queryBuilder.append(" AND p.price >= :minPrice");
        }
        if (filter.getMaxPrice() != null) {
            queryBuilder.append(" AND p.price <= :maxPrice");
        }

        if (filter.getCategoryId() != null) {
            queryBuilder.append(" AND p.category.categoryid = :categoryId");
        }

        if (filter.getSupplierId() != null) {
            queryBuilder.append(" AND p.supplier.supplierid = :supplierId");
        }

        TypedQuery<Product> query = entityManager.createQuery(queryBuilder.toString(), Product.class);

        if(active){
            query.setParameter("active", ActivityEnum.ACTIVE.getString());
        }
        if (filter.getMinQuantity() != null) {
            query.setParameter("minQuantity", filter.getMinQuantity());
        }
        if (filter.getMaxQuantity() != null) {
            query.setParameter("maxQuantity", filter.getMaxQuantity());
        }
        if (filter.getMinPrice() != null) {
            query.setParameter("minPrice", filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null) {
            query.setParameter("maxPrice", filter.getMaxPrice());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.getSupplierId() != null) {
            query.setParameter("supplierId", filter.getSupplierId());
        }

        return query.getResultList();
    }

}
