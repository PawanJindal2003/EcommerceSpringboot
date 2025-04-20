package com.Project.ecommerce.utils.validator;

import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.DeletedProductException;
import com.Project.ecommerce.exceptions.customExceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Component;

@Component
public class ProductUtil {

    public void validateIsDeletedProduct(Product product){
        if(product.getIsDeleted()){
            throw new DeletedProductException("Product is deleted please ask admin to add it");
        }
    }

    public void validateIsSellerProduct(Seller seller, Product product){
        if(!product.getSeller().getId().equals(seller.getId())){
            throw new UnauthorizedAccessException("You do not have permission to view this product.");
        }
    }
}
