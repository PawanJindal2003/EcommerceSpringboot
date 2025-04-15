package com.Project.ecommerce.services.seller;

import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.co.seller.UpdateProfileCO;
import com.Project.ecommerce.dto.seller.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.DuplicateGSTException;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.user.AddressRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import com.Project.ecommerce.utils.ImageUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class SellerService {
    private JwtService jwtService;
    private SellerRepository sellerRepository;
    private AddressRepository addressRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ImageUtil imageUtil;
    private MessageSource messageSource;
    @Autowired
    public SellerService(JwtService jwtService, SellerRepository sellerRepository, AddressRepository addressRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ImageUtil imageUtil, MessageSource messageSource){
        this.jwtService = jwtService;
        this.sellerRepository = sellerRepository;
        this.addressRepository = addressRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.imageUtil = imageUtil;
        this.messageSource = messageSource;
    }
    public ViewProfileDTO viewProfile(HttpServletRequest request){
        String accessToken = null;
        if(request.getCookies()!=null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        ViewProfileDTO viewProfileDTO = new ViewProfileDTO();
        String email = jwtService.extractEmail(accessToken);
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException(messageSource.getMessage("seller.not.found", null, request.getLocale())));

        viewProfileDTO.setId(seller.getId());
        viewProfileDTO.setFirstName(seller.getFirstName());
        viewProfileDTO.setLastName(seller.getLastName());
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

    public String updateProfile(HttpServletRequest request, UpdateProfileCO updateProfileCO, MultipartFile multipartFile){
        String accessToken = null;
        if(request.getCookies()!=null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        String email = jwtService.extractEmail(accessToken);
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException(messageSource.getMessage("seller.not.found", null, request.getLocale())));

        if(updateProfileCO!=null) {
            Optional.ofNullable(updateProfileCO.getFirstName()).ifPresent(seller::setFirstName);
            Optional.ofNullable(updateProfileCO.getMiddleName()).ifPresent(seller::setMiddleName);
            Optional.ofNullable(updateProfileCO.getLastName()).ifPresent(seller::setLastName);
            Optional.ofNullable(updateProfileCO.getCompanyName()).ifPresent(seller::setCompanyName);
            //not printing "enter a unique GST", security issue
            if (sellerRepository.findByGST(updateProfileCO.getGST()).isPresent()) {
                throw new DuplicateGSTException(messageSource.getMessage("gst.duplicate", null, request.getLocale()));
            }

            Optional.ofNullable(updateProfileCO.getGST()).ifPresent(seller::setGST);
            Optional.ofNullable(updateProfileCO.getCompanyContact()).ifPresent(seller::setCompanyContact);
            Optional.ofNullable(updateProfileCO.getCompanyName()).ifPresent(seller::setCompanyName);
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            imageUtil.saveImage(multipartFile, seller);
        }
        sellerRepository.save(seller);
        return messageSource.getMessage("profile.update.success", null, request.getLocale());
    }

    public String updatePassword(HttpServletRequest request, UpdatePasswordCO updatePasswordCO){
        // check if password and confirm password are not different
        if(!updatePasswordCO.getPassword().equals(updatePasswordCO.getConfirmPassword())){
            throw new ConfirmPasswordMismatchException(messageSource.getMessage("password.confirm.mismatch", null, request.getLocale()));
        }
        String accessToken = null;
        if(request.getCookies()!=null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        String email = jwtService.extractEmail(accessToken);
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("Seller not found"));

        //updating password
        seller.setPassword(bCryptPasswordEncoder.encode(updatePasswordCO.getPassword()));
        //updating passwordUpdateTime
        seller.setPasswordUpdateDate(Date.from(Instant.now()));

        sellerRepository.save(seller);

        return messageSource.getMessage("password.update.success", null, request.getLocale());
    }

    public String updateAddress(HttpServletRequest request, String addressId, UpdateAddressCO updateAddressCO){
        //is addressId existing
        if(addressRepository.findById(addressId).isPresent()){
            return messageSource.getMessage("address.not.found", null, request.getLocale());
        }
        //Doubt: validating addressId?

        String accessToken = null;
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("accessToken")){
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        String email = jwtService.extractEmail(accessToken);
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("Seller not found"));
        Address address = new Address();

        if(seller.getAddresses().get(0) != null){
            Optional.ofNullable(updateAddressCO.getAddressLine()).ifPresent(address::setAddressLine);
            Optional.ofNullable(updateAddressCO.getCity()).ifPresent(address::setCity);
            Optional.ofNullable(updateAddressCO.getState()).ifPresent(address::setState);
            Optional.ofNullable(updateAddressCO.getCountry()).ifPresent(address::setCountry);
            Optional.ofNullable(updateAddressCO.getZipCode()).ifPresent(address::setZipCode);
        }

        sellerRepository.save(seller);

        return messageSource.getMessage("address.update.success", null, request.getLocale());
    }
}