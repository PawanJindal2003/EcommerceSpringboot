package com.Project.ecommerce.utils.validator;

import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.DeletedProductException;
import com.Project.ecommerce.exceptions.customExceptions.InactiveResourceException;
import com.Project.ecommerce.exceptions.customExceptions.InvalidResourceException;
import com.Project.ecommerce.exceptions.customExceptions.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductUtil {
    private final MessageSource messageSource;
    public void validateIsDeletedProduct(Product product) {
        if (product.getIsDeleted()) {
            throw new DeletedProductException(messageSource.getMessage("productUtil.product.deleted", null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateIsSellerProduct(Seller seller, Product product) {
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new UnauthorizedAccessException(messageSource.getMessage("productUtil.product.unauthorized.access", null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateIsActiveProduct(Product product) {
        if (!product.getIsActive()) {
            throw new InactiveResourceException(messageSource.getMessage("productUtil.product.inactive", null, LocaleContextHolder.getLocale()));
        }
    }

    public void containsValidProductVariation(Product product) {
        List<ProductVariation> productVariations = product.getProductVariations();
        boolean isValid = productVariations.stream().anyMatch(productVariation -> Boolean.TRUE.equals(productVariation.getIsActive()));

        if(!isValid){
            throw new InvalidResourceException(messageSource.getMessage("productUtil.product.no.variation", null, LocaleContextHolder.getLocale()));
        }
    }
}
