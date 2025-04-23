package com.Project.ecommerce.services.category;

import com.Project.ecommerce.co.category.category.MetadataValueCategoryCO;
import com.Project.ecommerce.co.category.category.UpdateCategoryCO;
import com.Project.ecommerce.dto.category.admin.CategoryMetaFieldsDTO;
import com.Project.ecommerce.dto.category.admin.CategoryMetadataFieldDTO;
import com.Project.ecommerce.dto.category.admin.CategoryResponseDTO;
import com.Project.ecommerce.dto.category.admin.SubCategoryResponseDTO;
import com.Project.ecommerce.dto.category.customer.CustomerCategoryResponseDTO;
import com.Project.ecommerce.dto.category.customer.CustomerFilterCategoryDTO;
import com.Project.ecommerce.dto.category.customer.PriceRangeDTO;
import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.category.CategoryMetaDataField;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValuesId;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.repositories.category.CategoryMetaDataFieldRepository;
import com.Project.ecommerce.repositories.category.CategoryMetaDataFieldValuesRepository;
import com.Project.ecommerce.repositories.category.CategoryRepository;
import com.Project.ecommerce.repositories.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryMetaDataFieldRepository categoryMetaDataFieldRepository;
    private final MessageSource messageSource;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMetaDataFieldValuesRepository categoryMetaDataFieldValuesRepository;

    public List<String> addCategoryMetaDataField(String value) {
        logger.info("Attempting to add CategoryMetaDataField with value: {}", value);
        if (categoryMetaDataFieldRepository.findByName(value).isPresent()) {
            throw new DuplicateResourceException(messageSource.getMessage("categoryMetaDataFields.duplicate", null, LocaleContextHolder.getLocale()));
        }
        CategoryMetaDataField categoryMetaDataField = new CategoryMetaDataField();
        categoryMetaDataField.setName(value.toLowerCase());
        categoryMetaDataFieldRepository.save(categoryMetaDataField);
        logger.info("CategoryMetaDataField with value: {} added successfully.", value);

        return List.of(categoryMetaDataField.getId(), messageSource.getMessage("categoryMetaDataFields.added", null, LocaleContextHolder.getLocale()));
    }

    public CategoryMetaFieldsDTO getAllCategoryMetaDataField(int pageNo, int pageSize, String direction, String sortField) {
        logger.info("Retrieving all CategoryMetaDataFields");

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<CategoryMetaDataField> list = categoryMetaDataFieldRepository.findAll(pageable);

        CategoryMetaFieldsDTO dto = new CategoryMetaFieldsDTO();
        List<String> fields = list.getContent().stream().map(CategoryMetaDataField::getName).toList();
        dto.setFields(fields);
        logger.info("Retrieved {} CategoryMetaDataFields", fields.size());
        return dto;
    }

    public CategoryMetaFieldsDTO getAllCategoryMetaDataFieldByName(int pageNo, int pageSize, String direction, String sortField, String name) {
        logger.info("Retrieving all CategoryMetaDataFields by name");
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<CategoryMetaDataField> list = categoryMetaDataFieldRepository.findAllByName(name, pageable);

        CategoryMetaFieldsDTO dto = new CategoryMetaFieldsDTO();
        List<String> fields = list.getContent().stream().map(CategoryMetaDataField::getName).toList();
        dto.setFields(fields);
        logger.info("Retrieved {} CategoryMetaDataFields", fields.size());
        return dto;
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

    public String addCategory(String name) {
        logger.info("Attempting to add root category with name: {}", name);
        //unique category at root level
        if (isUniqueRootCategory(name)) {
            throw new DuplicateResourceException(messageSource.getMessage("duplicate.root.category", null, LocaleContextHolder.getLocale()));
        }

        Category category = new Category();
        category.setName(name.toLowerCase());
        categoryRepository.save(category);
        logger.info("Root category with name: {} added successfully.", name);
        return messageSource.getMessage("root.category.added", null, LocaleContextHolder.getLocale());
    }

    public String addSubCategory(String parentCategoryId, String name) {
        logger.info("Attempting to add subcategory with name: {} under parentCategoryId: {}", name, parentCategoryId);
        Category parentCategory = categoryRepository.findById(parentCategoryId).orElseThrow();

        if (!isUniqueCategory(parentCategoryId, name)) {
            throw new DuplicateResourceException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
        }

        //if associated with products
        //if categoryId present in product table then cannot add subcategory
        if (productRepository.findByCategoryId(parentCategoryId).isPresent()) {
            throw new CategoryAssignedToProductException(messageSource.getMessage("category.assigned.to.products", null, LocaleContextHolder.getLocale()));
        }

        Category category = new Category();
        category.setName(name.toLowerCase());
        //setting parent's category isLeafCategory to false;
        parentCategory.setIsLeafCategory(false);
        category.setParentCategory(parentCategory);

        categoryRepository.save(category);
        logger.info("Subcategory with name: {} added successfully under parent category with ID: {}", name, parentCategoryId);
        return messageSource.getMessage("sub.category.added", null, LocaleContextHolder.getLocale());
    }

    public CategoryResponseDTO saveCategoryInDTO(String id, Category category, List<CategoryMetaDataFieldValues> categoryMetaDataFieldValues) {
        logger.info("Saving category response DTO for category with ID: {}", id);

        CategoryResponseDTO dto = new CategoryResponseDTO();
        //name and id
        dto.setId(category.getId());
        dto.setName(category.getName());


        //parentChain
        Category parentCategory = category.getParentCategory();
        List<SubCategoryResponseDTO> parentsDTO = new ArrayList<>();
        if (parentCategory == null) {
            parentsDTO.add(null);
        }
        else {
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

        //meta data fields
        List<CategoryMetadataFieldDTO> metadataFieldDTOs = new ArrayList<>();
        for(CategoryMetaDataFieldValues fieldValue : categoryMetaDataFieldValues){
            CategoryMetadataFieldDTO metadataFieldDTO = new CategoryMetadataFieldDTO();
            metadataFieldDTO.setFieldName(fieldValue.getCategoryMetaDataField().getName());
            metadataFieldDTO.setFieldValues(fieldValue.getValue());
            metadataFieldDTOs.add(metadataFieldDTO);
        }
        dto.setMetaDataFields(metadataFieldDTOs);
        logger.info("Category response DTO for category ID: {} saved successfully.", id);
        return dto;
    }

    public CategoryResponseDTO getCategory(String id) {
        logger.info("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id).orElseThrow();
        List<CategoryMetaDataFieldValues> fieldValues = category.getMetadataFieldValues();
        logger.debug("Constructed CategoryResponseDTO");
        return saveCategoryInDTO(id, category, fieldValues);
    }

    public List<CategoryResponseDTO> getAllCategories(int pageNo, int pageSize, String direction, String sortField) {
        logger.info("Fetching all categories");
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<CategoryResponseDTO> categoriesDTO = new ArrayList<>();

        for (Category category : categories.getContent()) {
            List<CategoryMetaDataFieldValues> fieldValues = category.getMetadataFieldValues();
            CategoryResponseDTO categoryDTO = saveCategoryInDTO(category.getId(), category, fieldValues);
            categoriesDTO.add(categoryDTO);
        }
        logger.debug("Returning {} categories", categoriesDTO.size());
        return categoriesDTO;
    }

    public List<CategoryResponseDTO> getAllCategoriesByName(int pageNo, int pageSize, String direction, String sortField, String name) {
        logger.info("Fetching categories with name: '{}' - pageNo: {}, pageSize: {}, sort: {} {}", name, pageNo, pageSize, sortField, direction);
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<Category> categories = categoryRepository.findAllByName(name, pageable);
        List<CategoryResponseDTO> categoriesDTO = new ArrayList<>();

        for (Category category : categories.getContent()) {
            List<CategoryMetaDataFieldValues> fieldValues = category.getMetadataFieldValues();
            CategoryResponseDTO categoryDTO = saveCategoryInDTO(category.getId(), category, fieldValues);
            categoriesDTO.add(categoryDTO);
        }
        logger.debug("Found {} categories with name '{}'", categoriesDTO.size(), name);
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
        logger.info("Request received to update category. ID: {}, New Name: {}", id, name);

        Category category = categoryRepository.findById(id).orElseThrow();

        //validating
        Category parentCategory = category.getParentCategory();

        //if root category name changes
        if (parentCategory == null || parentCategory.getId() == null) {
            //check other root categories
            List<Category> rootCategories = categoryRepository.findAllByParentCategoryIdIsNull().orElseThrow();
            for(Category rootCategory: rootCategories){
                if(rootCategory.getName().equals(name)){
                    throw new DuplicateResourceException(messageSource.getMessage("duplicate.root.category", null, LocaleContextHolder.getLocale()));
                }
            }
            //check for whole subtree
            if(!bfs(category, name)){
                throw new DuplicateResourceException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
            }
        }
        else {
            //if subcategory name changes
            // 1. check parents
            Category checkParent = parentCategory;
            while (checkParent != null) {
                if (checkParent.getName().equals(name)) {
                    throw new DuplicateResourceException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
                }
                checkParent = checkParent.getParentCategory();
            }

            // 2. check for real siblings
            List<Category> realSiblings = categoryRepository.findAllByParentCategoryId(parentCategory.getId()).orElseThrow();
            for (Category realSibling : realSiblings) {
                if (realSibling.getName().equals(name)) {
                    throw new DuplicateResourceException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
                }
            }

            // 3. check whole subtree
            if(!bfs(category, name)){
                throw new DuplicateResourceException(messageSource.getMessage("duplicate.sub.category", null, LocaleContextHolder.getLocale()));
            }
        }

        // 4. update name
        category.setName(name);

        categoryRepository.save(category);
        logger.info("Category ID {} updated successfully to '{}'", id, name);
        return "Category name updated successfully";
    }

    public String addMetadataCategory(MetadataValueCategoryCO metadataValueCategoryCO){
        String categoryId = metadataValueCategoryCO.getCategoryId();
        String metaDataFieldId = metadataValueCategoryCO.getMetaDataFieldId();
        List<String> values = metadataValueCategoryCO.getValues();
        logger.info("Request to add metadata for CategoryID: {}, MetadataFieldID: {}", categoryId, metaDataFieldId);

        //checking validity of id
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new InactiveResourceException("Please enter a valid id"));
        CategoryMetaDataField categoryMetaDataField = categoryMetaDataFieldRepository.findById(metaDataFieldId).orElseThrow(()-> new InactiveResourceException("Please enter a valid id"));

        //checking all elements are unique
        values.replaceAll(String::toLowerCase);

        Set<String> valueSet = new HashSet<>(values);
        if(valueSet.size() < values.size()){
            throw new DuplicateResourceException("Duplicate values found in field values");
        }

        //adding metadata field values in leaf category only
        //check if category is leaf category or not
        List<Category> children = categoryRepository.findAllByParentCategoryId(categoryId).orElseThrow();
        if(!children.isEmpty()){
            logger.warn("Category ID {} is not a leaf category, cannot assign metadata values", categoryId);
            throw new NonLeafCategoryException("Cannot add metadata field values to a non leaf category");
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
        logger.info("Metadata values successfully added to CategoryID: {} for MetadataFieldID: {}", categoryId, metaDataFieldId);

        return "Category meta data field values added successfully for provided category and meta data field";
    }

    public String updateMetadataCategory(MetadataValueCategoryCO metadataValueCategoryCO){
        logger.info("Request to update metadata values");
        String categoryId = metadataValueCategoryCO.getCategoryId();
        String metaDataFieldId = metadataValueCategoryCO.getMetaDataFieldId();
        List<String> newValues = metadataValueCategoryCO.getValues();

        //checking validity of id
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new InactiveResourceException("Please enter a valid id"));
        CategoryMetaDataField categoryMetaDataField = categoryMetaDataFieldRepository.findById(metaDataFieldId).orElseThrow(()-> new InactiveResourceException("Please enter a valid id"));

        // provided metadata field should be linked with the provided category
        CategoryMetaDataFieldValues categoryMetaDataFieldValues = categoryMetaDataFieldValuesRepository.findByCategoryMetaDataFieldId(metaDataFieldId).orElseThrow(()->new FieldNotAssociatedException("Provided metadata field is not associated with the category"));

        // new values should be unique for category - metadata field combination
        newValues.replaceAll(String::toLowerCase);

        Set<String> newValueSet = new HashSet<>(newValues);
        //unique values are passed in list
        if (newValueSet.size() < newValues.size()) {
            throw new DuplicateResourceException("Duplicate values found in field values");
        }

        Set<String> existingValueSet = new HashSet<>(Arrays.asList(
                categoryMetaDataFieldValues.getValue().split(",")
        ));

        // skipping duplicates (oldSet + newSet)
        existingValueSet.addAll(newValueSet);

        categoryMetaDataFieldValues.setValue(String.join(",", existingValueSet));

        categoryMetaDataFieldValuesRepository.save(categoryMetaDataFieldValues);
        logger.info("Successfully updated metadata values for CategoryID: {}, MetadataFieldID: {}", categoryId, metaDataFieldId);
        return "New values added successfully ";
    }

    //seller service
    public List<CategoryResponseDTO> getSellerCategories(){
        logger.info("Fetching all seller leaf categories.");
        List<Category> allCategories = categoryRepository.findAll();
        List<Category> leafCategories = new ArrayList<>();
        for(Category category:allCategories){
            if(category.getIsLeafCategory()){
                leafCategories.add(category);
            }
        }
        logger.debug("Found {} leaf categories.", leafCategories.size());
        List<CategoryResponseDTO> leafCategoriesDTO = new ArrayList<>();
        for(Category leafCategory : leafCategories){
            //insert into list of DTO
            List<CategoryMetaDataFieldValues> fieldValues = leafCategory.getMetadataFieldValues();
            CategoryResponseDTO categoryDTO = saveCategoryInDTO(leafCategory.getId(), leafCategory, fieldValues);
            leafCategoriesDTO.add(categoryDTO);
        }

        //return list of DTO
        return leafCategoriesDTO;
    }

    //customer service
    public List<CustomerCategoryResponseDTO> getCustomerCategories(String categoryId){
        logger.info("Fetching customer subcategories for parent category ID: {}", categoryId);
        //validating categoryId
        List<Category> subCategories = categoryRepository.findAllByParentCategoryId(categoryId).orElseThrow();

        //get list of immediate subcategories
        List<CustomerCategoryResponseDTO> subCategoriesDTOs = new ArrayList<>();
        for(Category subCategory:subCategories){
            CustomerCategoryResponseDTO subCategoryDTO = new CustomerCategoryResponseDTO();
            subCategoryDTO.setId(subCategory.getId());
            subCategoryDTO.setName(subCategory.getName());
            subCategoriesDTOs.add(subCategoryDTO);
        }
        logger.debug("Returning {} subcategories for category ID: {}", subCategoriesDTOs.size(), categoryId);
        return subCategoriesDTOs;
    }

    public List<CustomerCategoryResponseDTO> getCustomerRootCategories(){
        logger.info("Fetching all customer root categories.");
        List<Category> rootCategories = categoryRepository.findAllByParentCategoryIdIsNull().orElseThrow();
        List<CustomerCategoryResponseDTO> rootCategoriesDTOs = new ArrayList<>();
        for(Category rootCategory:rootCategories){

            CustomerCategoryResponseDTO subCategoryDTO = new CustomerCategoryResponseDTO();
            subCategoryDTO.setId(rootCategory.getId());
            subCategoryDTO.setName(rootCategory.getName());
            rootCategoriesDTOs.add(subCategoryDTO);
        }
        logger.debug("Returning {} root categories.", rootCategoriesDTOs.size());
        return rootCategoriesDTOs;
    }

    public CustomerFilterCategoryDTO getFilteredCategories(String categoryId){
        logger.info("Fetching filtered category data for category ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category not found"));
        List<Category> associatedCategories = new ArrayList<>();

        if(category.getIsLeafCategory()){
            associatedCategories.add(category);
        }
        else{
            List<Category> leafCategories = findAssociatedLeafCategories(category);
            associatedCategories.addAll(leafCategories);
        }

        logger.debug("Found {} associated leaf categories for filtering.", associatedCategories.size());

        CustomerFilterCategoryDTO customerFilterCategoryDTO = new CustomerFilterCategoryDTO();

        List<CategoryMetadataFieldDTO> allMetadata = new ArrayList<>();
        List<String> brands = new ArrayList<>();
        PriceRangeDTO priceRangeDTO = new PriceRangeDTO();

        long minPrice = Long.MAX_VALUE;
        long maxPrice = Long.MIN_VALUE;

        for(Category associatedCategory: associatedCategories){
            brands = associatedCategory.getProducts().stream().map(product -> product.getBrand()).collect(Collectors.toList());

            allMetadata = associatedCategory.getMetadataFieldValues().stream().map(cmfv->{
                CategoryMetadataFieldDTO categoryMetadataFieldDTO = new CategoryMetadataFieldDTO();
                categoryMetadataFieldDTO.setFieldName(cmfv.getCategoryMetaDataField().getName());
                categoryMetadataFieldDTO.setFieldValues(cmfv.getValue());
                return categoryMetadataFieldDTO;
            }).collect(Collectors.toList());

            List<Product> products = associatedCategory.getProducts();
            for(Product product:products){
                for(ProductVariation productVariation : product.getProductVariations()){
                    if(productVariation.getPrice() < minPrice){
                        minPrice = productVariation.getPrice();
                    }
                    if(productVariation.getPrice() > maxPrice){
                        maxPrice = productVariation.getPrice();
                    }
                }
            }
        }
        priceRangeDTO.setMinPrice(minPrice);
        priceRangeDTO.setMaxPrice(maxPrice);

        customerFilterCategoryDTO.setBrands(brands);
        customerFilterCategoryDTO.setMetadata(allMetadata);
        customerFilterCategoryDTO.setPriceRange(priceRangeDTO);
        logger.info("Filtered data prepared for category ID: {}", categoryId);

        return customerFilterCategoryDTO;
    }
    private List<Category> findAssociatedLeafCategories(Category category){
        List<Category> leafCategories = new ArrayList<>();
        Queue<Category> queue = new LinkedList<>();
        queue.add(category);
        while (!queue.isEmpty()){
            Category currentCategory = queue.poll();

            if(currentCategory.getIsLeafCategory()){
                leafCategories.add(currentCategory);
            }
            else{
                queue.addAll(categoryRepository.findAllByParentCategoryId(currentCategory.getId()).orElseThrow(()-> new InactiveResourceException("Invalid category id provided")));
            }
        }
        return leafCategories;
    }
}