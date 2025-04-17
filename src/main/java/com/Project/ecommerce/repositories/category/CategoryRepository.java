package com.Project.ecommerce.repositories.category;

import com.Project.ecommerce.entities.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<List<Category>> findAllByParentCategoryIdIsNull();
    Boolean existsByNameAndParentCategoryIdIsNull(String name);

    //immediate child categories
    Optional<List<Category>> findAllByParentCategoryId(String id);
}
