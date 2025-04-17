package com.Project.ecommerce.services.category;

import com.Project.ecommerce.co.category.category.MetadataValueCategoryCO;
import com.Project.ecommerce.co.category.category.UpdateCategoryCO;
import com.Project.ecommerce.dto.category.CategoryMetadataFieldValueDTO;
import com.Project.ecommerce.dto.category.CategoryResponseDTO;
import com.Project.ecommerce.dto.category.SubCategoryResponseDTO;
import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.category.CategoryMetaDataField;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValuesId;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.repositories.category.CategoryMetaDataFieldRepository;
import com.Project.ecommerce.repositories.category.CategoryMetaDataFieldValuesRepository;
import com.Project.ecommerce.repositories.category.CategoryRepository;
import com.Project.ecommerce.repositories.product.ProductRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoryService {
    private CategoryMetaDataFieldRepository categoryMetaDataFieldRepository;
    private MessageSource messageSource;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private CategoryMetaDataFieldValuesRepository categoryMetaDataFieldValuesRepository;

    public CategoryService(CategoryMetaDataFieldRepository categoryMetaDataFieldRepository, MessageSource messageSource, CategoryRepository categoryRepository, ProductRepository productRepository, CategoryMetaDataFieldValuesRepository categoryMetaDataFieldValuesRepository) {
        this.categoryMetaDataFieldRepository = categoryMetaDataFieldRepository;
        this.messageSource = messageSource;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryMetaDataFieldValuesRepository = categoryMetaDataFieldValuesRepository;
    }

    public List<String> addCategoryMetaDataField(String value) {
        if (categoryMetaDataFieldRepository.findByName(value).isPresent()) {
            throw new DuplicateCategoryMetaDataFieldException(messageSource.getMessage("categoryMetaDataFields.duplicate", null, LocaleContextHolder.getLocale()));
        }
        CategoryMetaDataField categoryMetaDataField = new CategoryMetaDataField();
        categoryMetaDataField.setName(value.toLowerCase());
        categoryMetaDataFieldRepository.save(categoryMetaDataField);

        return List.of(categoryMetaDataField.getId(), messageSource.getMessage("categoryMetaDataFields.added", null, LocaleContextHolder.getLocale()));
    }

    public CategoryMetadataFieldValueDTO getAllCategoryMetaDataField(int pageNo, int pageSize, String direction, String sortField) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<CategoryMetaDataField> list = categoryMetaDataFieldRepository.findAll(pageable);

        CategoryMetadataFieldValueDTO dto = new CategoryMetadataFieldValueDTO();
        List<String> fields = list.getContent().stream().map(CategoryMetaDataField::getName).toList();
        dto.setFields(fields);
        return dto;
    }

    public String addCategory(String name) {
        //unique category at root level
        if (isUniqueRootCategory(name)) {
            throw new DuplicateRootCategoryException(messageSource.getMessage("duplicate.root.category", null, LocaleContextHolder.getLocale()));
        }

        Category category = new Category();
        category.setName(name.toLowerCase());
        categoryRepository.save(category);
        return messageSource.getMessage("root.category.added", null, LocaleContextHolder.getLocale());
    }

    boolean isUniqueRootCategory(String name) {
        return categoryRepository.existsByNameAndParentCategoryIdIsNull(name);
    }

    boolean isUniqueCategory(String parentCategoryId, String name) {
        Category parentCategory = categoryRepository.findById(parentCategoryId).orElseThrow();
        //unique at breadth
        List<Category> childCategories = categoryRepository.findAllByParentCategoryId(parentCategoryId).orElseThrow();
        for (Category childCategory : childCategories) {
            if (childCategory.getName().equals(name)) {
                return false;
            }
        }

        //unique at depth
        while (parentCategory != null) {
            if (parentCategory.getName().equals(name)) {
                return false;
            }
            parentCategory = parentCategory.getParentCategory();
        }
        return true;
    }

    public String addSubCategory(String parentCategoryId, String name) {
        Category parentCategory = categoryRepository.findById(parentCategoryId).orElseThrow();

        if (!isUniqueCategory(parentCategoryId, name)) {
            throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
        }

        //if associated with products
        //if categoryId present in product table then cannot add subcategory
        if (productRepository.findByCategoryId(parentCategoryId).isPresent()) {
            throw new CategoryAssignedToProductException(messageSource.getMessage("category.assigned.to.products", null, LocaleContextHolder.getLocale()));
        }

        Category category = new Category();
        category.setName(name.toLowerCase());
        category.setParentCategory(parentCategory);

        categoryRepository.save(category);

        return messageSource.getMessage("sub.category.added", null, LocaleContextHolder.getLocale());
    }

    CategoryResponseDTO saveCategoryInDTO(String id, Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        //name and id
        dto.setId(category.getId());
        dto.setName(category.getName());

        //parentChain
        Category parentCategory = category.getParentCategory();
        List<SubCategoryResponseDTO> parentsDTO = new ArrayList<>();
        if (parentCategory == null) {
            parentsDTO.add(null);
        } else {
            while (parentCategory.getParentCategory() != null) {
                SubCategoryResponseDTO parentDTO = new SubCategoryResponseDTO();
                parentDTO.setId(parentCategory.getId());
                parentDTO.setName(parentCategory.getName());
                parentDTO.setParentId(parentCategory.getParentCategory().getId());

                parentsDTO.add(parentDTO);

                //going up
                parentCategory = parentCategory.getParentCategory();
            }
            SubCategoryResponseDTO rootParentDTO = new SubCategoryResponseDTO();
            rootParentDTO.setId(parentCategory.getId());
            rootParentDTO.setName(parentCategory.getName());
            rootParentDTO.setParentId("NULL");
            parentsDTO.add(rootParentDTO);
        }

        dto.setParentChain(parentsDTO);

        //subCategories
        List<Category> subCategories = categoryRepository.findAllByParentCategoryId(id).orElseThrow();
        List<SubCategoryResponseDTO> subCategoriesDTO = new ArrayList<>();
        for (Category subCategory : subCategories) {
            SubCategoryResponseDTO subCategoryDTO = new SubCategoryResponseDTO();
            subCategoryDTO.setId(subCategory.getId());
            subCategoryDTO.setName(subCategory.getName());
            subCategoryDTO.setParentId(category.getId());

            subCategoriesDTO.add(subCategoryDTO);
        }
        dto.setSubCategories(subCategoriesDTO);

        return dto;
    }

    public CategoryResponseDTO getCategory(String id) {
        Category category = categoryRepository.findById(id).orElseThrow();

        return saveCategoryInDTO(id, category);
    }

    public List<CategoryResponseDTO> getAllCategories(int pageNo, int pageSize, String direction, String sortField) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<CategoryResponseDTO> categoriesDTO = new ArrayList<>();

        for (Category category : categories.getContent()) {
            CategoryResponseDTO categoryDTO = saveCategoryInDTO(category.getId(), category);
            categoriesDTO.add(categoryDTO);
        }
        return categoriesDTO;
    }

    boolean bfs(Category category, String name){
        Queue<Category> queue = new LinkedList<>();

        queue.add(category);
        while (!queue.isEmpty()){
            Category currentCategory = queue.poll();

            if(currentCategory.getName().equals(name)){
                return false;
            }

            String currentCategoryId = currentCategory.getId();
            List<Category> subCategories = categoryRepository
                    .findAllByParentCategoryId(currentCategoryId).orElse(Collections.emptyList());

            queue.addAll(subCategories);
        }
        return true;
    }

    public String updateCategory(UpdateCategoryCO updateCategoryCO) {
        String id = updateCategoryCO.getId();
        String name = updateCategoryCO.getName();

        Category category = categoryRepository.findById(id).orElseThrow();

        //validating
        Category parentCategory = category.getParentCategory();

        //if root category name changes
        if (parentCategory == null || parentCategory.getId() == null) {
            //check other root categories
            List<Category> rootCategories = categoryRepository.findAllByParentCategoryIdIsNull().orElseThrow();
            for(Category rootCategory: rootCategories){
                if(rootCategory.getName().equals(name)){
                    throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.root.category", null, LocaleContextHolder.getLocale()));
                }
            }
            //check for whole subtree
            if(!bfs(category, name)){
                throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
            }
        }
        else {
            //if subcategory name changes
            // 1. check parents
            Category checkParent = parentCategory;
            while (checkParent != null) {
                if (checkParent.getName().equals(name)) {
                    throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
                }
                checkParent = checkParent.getParentCategory();
            }

            // 2. check for real siblings
            List<Category> realSiblings = categoryRepository.findAllByParentCategoryId(parentCategory.getId()).orElseThrow();
            for (Category realSibling : realSiblings) {
                if (realSibling.getName().equals(name)) {
                    throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
                }
            }

            // 3. check whole subtree
            if(!bfs(category, name)){
                throw new DuplicateSubCategoryException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
            }
        }

        // 4. update name
        category.setName(name);

        categoryRepository.save(category);

        return "Category name updated successfully";
    }

    public String addMetadataCategory(MetadataValueCategoryCO metadataValueCategoryCO){
        String categoryId = metadataValueCategoryCO.getCategoryId();
        String metaDataFieldId = metadataValueCategoryCO.getMetaDataFieldId();
        List<String> values = metadataValueCategoryCO.getValues();

        //checking validity of id
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new InvalidIdException("Please enter a valid id"));
        CategoryMetaDataField categoryMetaDataField = categoryMetaDataFieldRepository.findById(metaDataFieldId).orElseThrow(()-> new InvalidIdException("Please enter a valid id"));

        //checking all elements are unique
        values.replaceAll(String::toLowerCase);

        Set<String> valueSet = new HashSet<>(values);
        if(valueSet.size() < values.size()){
            throw new DuplicateMetadataFieldValuesException("Duplicate values found in field values");
        }

        //adding metadata field values in leaf category only
        //check if category is leaf category or not
        List<Category> children = categoryRepository.findAllByParentCategoryId(categoryId).orElseThrow();
        if(!children.isEmpty()){
            throw new NonLeafCategoryException("Cannot add metadata field values to a non leaf catgeory");
        }

        //setting composite key
        CategoryMetaDataFieldValuesId id = new CategoryMetaDataFieldValuesId(categoryId, metaDataFieldId);

        //setting entity
        CategoryMetaDataFieldValues categoryMetaDataFieldValues = new CategoryMetaDataFieldValues();

        //adding category in entity
        categoryMetaDataFieldValues.setId(id);
        categoryMetaDataFieldValues.setCategory(category);

        //adding categoryMetaDataField
        categoryMetaDataFieldValues.setCategoryMetaDataField(categoryMetaDataField);

        //adding values in comma separated manner
        categoryMetaDataFieldValues.setValue(String.join(",", values));

        categoryMetaDataFieldValuesRepository.save(categoryMetaDataFieldValues);
        return "Category meta data field values added successfully for provided category and meta data field";
    }

    public String updateMetadataCategory(MetadataValueCategoryCO metadataValueCategoryCO){
        String categoryId = metadataValueCategoryCO.getCategoryId();
        String metaDataFieldId = metadataValueCategoryCO.getMetaDataFieldId();
        List<String> newValues = metadataValueCategoryCO.getValues();

        //checking validity of id
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new InvalidIdException("Please enter a valid id"));
        CategoryMetaDataField categoryMetaDataField = categoryMetaDataFieldRepository.findById(metaDataFieldId).orElseThrow(()-> new InvalidIdException("Please enter a valid id"));

        // provided metadata field should be linked with the provided category
        CategoryMetaDataFieldValues categoryMetaDataFieldValues = categoryMetaDataFieldValuesRepository.findByCategoryMetaDataFieldId(metaDataFieldId).orElseThrow(()->new FieldNotAssociatedException("Provided metadata field is not associated with the category"));

        // new values should be unique for category - metadata field combination
        newValues.replaceAll(String::toLowerCase);

        Set<String> newValueSet = new HashSet<>(newValues);
        //unique values are passed in list
        if (newValueSet.size() < newValues.size()) {
            throw new DuplicateMetadataFieldValuesException("Duplicate values found in field values");
        }

        Set<String> existingValueSet = new HashSet<>(Arrays.asList(
                categoryMetaDataFieldValues.getValue().split(",")
        ));

        // skipping duplicates (oldSet + newSet)
        existingValueSet.addAll(newValueSet);

        categoryMetaDataFieldValues.setValue(String.join(",", existingValueSet));

        categoryMetaDataFieldValuesRepository.save(categoryMetaDataFieldValues);

        return "New values added successfully ";
    }
}