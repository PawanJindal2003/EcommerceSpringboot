package com.Project.ecommerce.services.category;

import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.category.CategoryMetaDataField;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.repositories.category.CategoryMetaDataFieldRepository;
import com.Project.ecommerce.repositories.category.CategoryRepository;
import com.Project.ecommerce.repositories.product.ProductRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private CategoryMetaDataFieldRepository categoryMetaDataFieldRepository;
    private MessageSource messageSource;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;

    public CategoryService(CategoryMetaDataFieldRepository categoryMetaDataFieldRepository, MessageSource messageSource, CategoryRepository categoryRepository, ProductRepository productRepository){
        this.categoryMetaDataFieldRepository = categoryMetaDataFieldRepository;
        this.messageSource = messageSource;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<String> addCategoryMetaDataField(String value){
        if(categoryMetaDataFieldRepository.findByName(value).isPresent()){
            throw new DuplicateCategoryMetaDataFieldException(messageSource.getMessage("categoryMetaDataFields.duplicate", null, LocaleContextHolder.getLocale()));
        }
        CategoryMetaDataField categoryMetaDataField = new CategoryMetaDataField();
        categoryMetaDataField.setName(value);
        categoryMetaDataFieldRepository.save(categoryMetaDataField);

        return List.of(categoryMetaDataField.getId(), messageSource.getMessage("categoryMetaDataFields.added", null, LocaleContextHolder.getLocale()));
    }

    public Page<CategoryMetaDataField> getAllCategoryMetaDataField(Pageable pageable){
        return categoryMetaDataFieldRepository.findAll(pageable);
    }

    public String addCategory(String name){
        //unique category at root level
        if(categoryRepository.existsByNameAndParentCategoryIdIsNull(name)){
            throw new DuplicateRootCategoryException(messageSource.getMessage("duplicate.root.category", null, LocaleContextHolder.getLocale()));
        }

        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
        return messageSource.getMessage("root.category.added", null, LocaleContextHolder.getLocale());
    }

    public String addSubCategory(String parentCategoryId, String name) {
        Category parentCategory = categoryRepository.findById(parentCategoryId).orElseThrow();

        //unique at breadth
        List<Category> childCategories = categoryRepository.findAllByParentCategoryId(parentCategoryId).orElseThrow();
        for(Category childCategory:childCategories){
            if(childCategory.getName().equals(name)){
                throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
            }
        }

        //unique at depth
        while (parentCategory != null) {
            if (parentCategory.getName().equals(name)) {
                throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
            }
            parentCategory = parentCategory.getParentCategory();
        }

        //if associated with products
        //if categoryId present in product table then cannot add subcategory
        if(productRepository.findByCategoryId(parentCategoryId).isPresent()){
            throw new CategoryAssignedToProductException(messageSource.getMessage("category.assigned.to.products", null, LocaleContextHolder.getLocale()));
        }

        Category category = new Category();
        category.setName(name);
        category.setParentCategory(parentCategory);

        categoryRepository.save(category);

        return messageSource.getMessage("sub.category.added", null, LocaleContextHolder.getLocale());
    }

    public List<Category> getCategory(String id){
        Category category = categoryRepository.findById(id).orElseThrow();
        List<Category> categories = new ArrayList<>();

        // adding parent directory
        List<Category> parentCategories = new ArrayList<>();
        Category parentCategory = category.getParentCategory();
        while(parentCategory!=null) {
            categories.add(parentCategory);
            parentCategory = parentCategory.getParentCategory();
        }

        // adding current category
        categories.add(category);

        // adding immediate child
        List<Category> childCategories = categoryRepository.findAllByParentCategoryId(id).orElseThrow();
        categories.addAll(childCategories);

        return categories;
    }

}
