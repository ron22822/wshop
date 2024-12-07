package com.example.wshop.controllers;

import com.example.wshop.constant.RoleEnum;
import com.example.wshop.dto.ProductDTO;
import com.example.wshop.dto.ProductFilter;
import com.example.wshop.model.User;
import com.example.wshop.service.ProductService;
import com.example.wshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService,UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = productService.getById(id);
        return ResponseEntity.ok(productDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProduct();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductDTO>> getAllActiveProducts() {
        List<ProductDTO> activeProducts = productService.getAllActiveProduct();
        return ResponseEntity.ok(activeProducts);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductDTO>> getAllFiltration(@RequestBody ProductFilter productFilter){
        User user = userService.getCurrentUser();
        List<ProductDTO> products;
        if(RoleEnum.ADMIN.getString().equals(user.getRole().getRolename())){
            products = productService.gatAllFiltration(productFilter,false);
        }
        else {
            products = productService.gatAllFiltration(productFilter,true);
        }
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProductById(@PathVariable Long id,@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProductById(id, productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted Product id: " + id);
    }
}

