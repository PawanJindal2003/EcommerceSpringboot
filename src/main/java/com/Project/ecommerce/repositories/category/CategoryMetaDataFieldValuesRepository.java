package com.Project.ecommerce.repositories.category;

import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValuesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryMetaDataFieldValuesRepository extends JpaRepository<CategoryMetaDataFieldValues, CategoryMetaDataFieldValuesId> {
    Optional<CategoryMetaDataFieldValues> findByCategoryMetaDataFieldId(String id);
}
