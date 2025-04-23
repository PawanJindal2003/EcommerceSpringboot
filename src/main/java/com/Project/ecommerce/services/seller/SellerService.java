package com.Project.ecommerce.services.seller;

import com.Project.ecommerce.co.seller.UpdateProfileCO;
import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.dto.seller.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.DuplicateResourceException;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.exceptions.customExceptions.UnauthorizedAccessException;
import com.Project.ecommerce.repositories.user.AddressRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import com.Project.ecommerce.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private static final Logger logger = LoggerFactory.getLogger(SellerService.class);
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageUtil imageUtil;
    private final MessageSource messageSource;

    public ViewProfileDTO viewProfile(Principal principal) {
        String email = principal.getName();
        logger.info("Fetching profile for seller: {}", email);

        Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));

        ViewProfileDTO viewProfileDTO = new ViewProfileDTO();
        viewProfileDTO.setId(seller.getId());
        if (seller.getMiddleName() == null) {
            viewProfileDTO.setName(seller.getFirstName() + " " + seller.getLastName());
        } else {
            viewProfileDTO.setName(seller.getFirstName() + " " + seller.getMiddleName() + " " + seller.getLastName());
        }
        viewProfileDTO.setCompanyContact(seller.getCompanyContact());
        viewProfileDTO.setCompanyName(seller.getCompanyName());
        viewProfileDTO.setGST(seller.getGST());
        viewProfileDTO.setIsActive(seller.getIsActive());

        //address
        viewProfileDTO.setAddressLine(seller.getAddresses().get(0).getAddressLine());
        viewProfileDTO.setCity(seller.getAddresses().get(0).getCity());
        viewProfileDTO.setCountry(seller.getAddresses().get(0).getCountry());
        viewProfileDTO.setState(seller.getAddresses().get(0).getState());
        viewProfileDTO.setZipCode(seller.getAddresses().get(0).getZipCode());
        viewProfileDTO.setProfilePicUrl(imageUtil.getImage(seller.getId()));

        return viewProfileDTO;
    }

    public String updateProfile(Principal principal, UpdateProfileCO updateProfileCO, MultipartFile multipartFile) {
        String email = principal.getName();
        logger.info("Updating profile for seller: {}", email);

        Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));

        if (updateProfileCO != null) {
            Optional.ofNullable(updateProfileCO.getFirstName()).ifPresent(seller::setFirstName);
            Optional.ofNullable(updateProfileCO.getMiddleName()).ifPresent(seller::setMiddleName);
            Optional.ofNullable(updateProfileCO.getLastName()).ifPresent(seller::setLastName);
            Optional.ofNullable(updateProfileCO.getCompanyName()).ifPresent(seller::setCompanyName);
            //not printing "enter a unique GST", security issue
            if (sellerRepository.findByGST(updateProfileCO.getGST()).isPresent()) {
                logger.warn("Duplicate GST provided for update by seller: {}", email);
                throw new DuplicateResourceException(messageSource.getMessage("gst.duplicate", null, LocaleContextHolder.getLocale()));
            }

            Optional.ofNullable(updateProfileCO.getGST()).ifPresent(seller::setGST);
            Optional.ofNullable(updateProfileCO.getCompanyContact()).ifPresent(seller::setCompanyContact);
            Optional.ofNullable(updateProfileCO.getCompanyName()).ifPresent(seller::setCompanyName);
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            logger.info("Saving profile picture for seller: {}", email);
            imageUtil.saveUserImage(multipartFile, seller);
        }
        sellerRepository.save(seller);
        logger.info("Profile updated successfully for seller: {}", email);
        return messageSource.getMessage("profile.update.success", null, LocaleContextHolder.getLocale());
    }

    public String updatePassword(Principal principal, UpdatePasswordCO updatePasswordCO) {
        logger.info("Password update requested for seller");
        // check if password and confirm password are not different
        if (!updatePasswordCO.getPassword().equals(updatePasswordCO.getConfirmPassword())) {
            throw new ConfirmPasswordMismatchException(messageSource.getMessage("password.confirm.mismatch", null, LocaleContextHolder.getLocale()));
        }
        String email = principal.getName();
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        //updating password
        seller.setPassword(bCryptPasswordEncoder.encode(updatePasswordCO.getPassword()));
        //updating passwordUpdateTime
        seller.setPasswordUpdateDate(Date.from(Instant.now()));

        sellerRepository.save(seller);
        logger.info("Password updated successfully for seller: {}", email);
        return messageSource.getMessage("password.update.success", null, LocaleContextHolder.getLocale());
    }

    public String updateAddress(Principal principal, String addressId, UpdateAddressCO updateAddressCO) {
        String email = principal.getName();
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        logger.info("Updating address for seller: {}", email);

        //validating address
        Address sellerAddress = seller.getAddresses().get(0);
        boolean isSellerAddress = sellerAddress.getId().equals(addressId);

        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("customer.address.not.found", null, LocaleContextHolder.getLocale())));
        if (!isSellerAddress) {
            logger.warn("Unauthorized address update attempt by seller: {}", email);
            throw new UnauthorizedAccessException("Invalid address id, please pass logged in seller's address id");
        }

        Optional.ofNullable(updateAddressCO.getAddressLine()).ifPresent(address::setAddressLine);
        Optional.ofNullable(updateAddressCO.getCity()).ifPresent(address::setCity);
        Optional.ofNullable(updateAddressCO.getState()).ifPresent(address::setState);
        Optional.ofNullable(updateAddressCO.getCountry()).ifPresent(address::setCountry);
        Optional.ofNullable(updateAddressCO.getZipCode()).ifPresent(address::setZipCode);

        addressRepository.save(address);
        logger.info("Address updated successfully for seller: {}", email);
        return messageSource.getMessage("address.update.success", null, LocaleContextHolder.getLocale());
    }
}