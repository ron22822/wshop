package com.example.wshop.service;

import com.example.wshop.dto.CategoryDTO;
import com.example.wshop.exception.ResourceNameAlreadyExistsException;
import com.example.wshop.exception.ResourceNotFoundException;
import com.example.wshop.model.Category;
import com.example.wshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDTO getById(Long id){
        return categoryRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(()  -> new ResourceNotFoundException("Category not found with Id: " + id));
    }

    public Page<CategoryDTO> getAllCategory(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(this::mapToDto);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        if(categoryRepository.findByCategoryname(categoryDTO.getCategoryname()).isPresent()){
            throw new ResourceNameAlreadyExistsException(
                    "Category with category name: "+categoryDTO.getCategoryname()
                            +" already exists");
        }
        Category category = mapToCategory(categoryDTO);
        category = categoryRepository.save(category);
        return mapToDto(category);
    }

    @Transactional
    public CategoryDTO updateCategoryById(Long id,CategoryDTO categoryDTO){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()  -> new ResourceNotFoundException("Category not found with Id: " + id));
        Optional<Category> categoryOptional = categoryRepository.findByCategoryname(categoryDTO.getCategoryname());
        if(categoryOptional.isPresent()){
            if(!id.equals(categoryOptional.get().getCategoryid())){
                throw new ResourceNameAlreadyExistsException(
                        "Category with category name: "+categoryDTO.getCategoryname()
                                +" already exists");
            }
        }
        category.setCategoryname(categoryDTO.getCategoryname());
        category.setInfo(categoryDTO.getInfo());
        Category categoryUpdate = categoryRepository.save(category);
        return mapToDto(categoryUpdate);
    }

    public void deleteCategoryById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()  -> new ResourceNotFoundException("Category not found with Id: " + id));
        categoryRepository.delete(category);
    }

    private Category mapToCategory(CategoryDTO categoryDTO){
        Category category = new Category();
        category.setCategoryname(categoryDTO.getCategoryname());
        category.setInfo(categoryDTO.getInfo());
        return category;
    }

    private CategoryDTO mapToDto(Category category){
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryid(category.getCategoryid());
        categoryDTO.setCategoryname(category.getCategoryname());
        categoryDTO.setInfo(category.getInfo());
        return categoryDTO;
    }

}
