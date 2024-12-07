package com.example.wshop.service;

import com.example.wshop.constant.ActivityEnum;
import com.example.wshop.dto.ProductDTO;
import com.example.wshop.dto.ProductFilter;
import com.example.wshop.exception.InvalidRequestDataException;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.Category;
import com.example.wshop.model.Product;
import com.example.wshop.model.Supplier;
import com.example.wshop.repository.CategoryRepository;
import com.example.wshop.repository.ProductRepository;
import com.example.wshop.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final OrderItemService orderItemService;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository
            , SupplierRepository supplierRepository, OrderItemService orderItemService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.orderItemService = orderItemService;
    }

    public ProductDTO getById(Long id){
        return productRepository.findProductWithCategoryAndSupplier(id)
                .map(this::mapToDto)
                .orElseThrow(()  -> new ResourceNotFoundException("Product not found with Id: " + id));
    }

    public List<ProductDTO> getAllProduct(){
        return productRepository.findAllProductsWithCategoryAndSupplier()
                .stream().map(this::mapToDto).toList();
    }

    public List<ProductDTO> getAllActiveProduct(){
        return productRepository.findAllActiveProductsWithCategoryAndSupplier()
                .stream().map(this::mapToDto).toList();
    }

    public List<ProductDTO> gatAllFiltration(ProductFilter productFilter,Boolean active){
        return productRepository.findByFilter(productFilter,active)
                .stream().map(this::mapToDto).toList();
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO){
        Product product = mapToProduct(productDTO);
        product = productRepository.save(product);
        return mapToDto(product);
    }

    @Transactional
    public ProductDTO updateProductById(Long id,ProductDTO productDTO){
        Product product = productRepository.findById(id).
                orElseThrow(()  -> new ResourceNotFoundException("Product not found with Id: " + id));
        product.setProductname(productDTO.getProductname());
        product.setPrice(productDTO.getPrice());
        product.setTotalquantity(productDTO.getTotalquantity());
        if(ActivityEnum.ACTIVE.getString().equals(productDTO.getActivity())
                || ActivityEnum.INACTIVE.getString().equals(productDTO.getActivity()) ){
            product.setActivity(productDTO.getActivity());
        } else{
            throw new InvalidRequestDataException("Undefined activity state");
        }
        Category category = categoryRepository.findByCategoryname(productDTO.getCategoryname())
                .orElseThrow(()  -> new InvalidRequestDataException("Invalid category name"));
        product.setCategory(category);
        Supplier supplier = supplierRepository.findBySuppliername(productDTO.getSuppliername())
                .orElseThrow(()  -> new InvalidRequestDataException("Invalid supplier name"));
        product.setSupplier(supplier);
        Product productUpdate = productRepository.save(product);
        return mapToDto(productUpdate);
    }

    public void deleteProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()  -> new ResourceNotFoundException("Product not found with Id: " + id));
        orderItemService.deleteAllOrderItemForProduct(product);
        productRepository.delete(product);
    }

    private ProductDTO mapToDto(Product product){
        ProductDTO productDTO = new ProductDTO();

        productDTO.setProductid(product.getProductid());
        productDTO.setProductname(product.getProductname());
        productDTO.setPrice(product.getPrice());
        productDTO.setTotalquantity(product.getTotalquantity());
        productDTO.setActivity(product.getActivity());
        if(product.getCategory() != null){
            productDTO.setCategoryname(product.getCategory().getCategoryname());
        }
        if(product.getSupplier() != null){
            productDTO.setSuppliername(product.getSupplier().getSuppliername());
        }
        return productDTO;
    }

    private Product mapToProduct(ProductDTO productDTO){
        Product product = new Product();
        product.setProductname(productDTO.getProductname());
        product.setPrice(productDTO.getPrice());
        product.setTotalquantity(productDTO.getTotalquantity());

        if(ActivityEnum.ACTIVE.getString().equals(productDTO.getActivity())
        || ActivityEnum.INACTIVE.getString().equals(productDTO.getActivity()) ){
            product.setActivity(productDTO.getActivity());
        } else{
            throw new InvalidRequestDataException("Activity state must be "+ActivityEnum.ACTIVE.getString()+" or "+ActivityEnum.INACTIVE.getString());
        }
        Category category = categoryRepository.findByCategoryname(productDTO.getCategoryname())
                .orElseThrow(()  -> new InvalidRequestDataException("Invalid category name"));
        Supplier supplier = supplierRepository.findBySuppliername(productDTO.getSuppliername())
                .orElseThrow(()  -> new InvalidRequestDataException("Invalid supplier name"));
        product.setCategory(category);
        product.setSupplier(supplier);
        return product;
    }
}
