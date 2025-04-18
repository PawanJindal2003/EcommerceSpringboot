package com.Project.ecommerce.repositories.category;

import com.Project.ecommerce.entities.category.CategoryMetaDataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryMetaDataFieldRepository extends JpaRepository<CategoryMetaDataField, String> {
    Optional<CategoryMetaDataField> findByName(String name);

    Page<CategoryMetaDataField> findAll(Pageable pageable);

    @Query("select c from CategoryMetaDataField c WHERE (lower(c.name) like lower(concat('%', :name, '%')))")
    Page<CategoryMetaDataField> findAllByName(@Param("name") String name, Pageable pageable);
}