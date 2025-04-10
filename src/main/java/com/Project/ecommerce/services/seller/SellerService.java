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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SellerService {
    private JwtService jwtService;
    private SellerRepository sellerRepository;
    private AddressRepository addressRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public SellerService(JwtService jwtService, SellerRepository sellerRepository, AddressRepository addressRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.jwtService = jwtService;
        this.sellerRepository = sellerRepository;
        this.addressRepository = addressRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        Seller seller = sellerRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("Seller not found"));

        viewProfileDTO.setId(seller.getID());
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
//        sellerProfileDTO.setImage(seller.getImage());

        return viewProfileDTO;
    }

    public String updateProfile(HttpServletRequest request, UpdateProfileCO updateProfileCO){
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

        Optional.ofNullable(updateProfileCO.getFirstName()).ifPresent(seller::setFirstName);
        Optional.ofNullable(updateProfileCO.getMiddleName()).ifPresent(seller::setMiddleName);
        Optional.ofNullable(updateProfileCO.getLastName()).ifPresent(seller::setLastName);
        Optional.ofNullable(updateProfileCO.getCompanyName()).ifPresent(seller::setCompanyName);
        //not printing "enter a unique GST", security issue
        if(sellerRepository.findByGST(updateProfileCO.getGST()).isPresent()){
            throw new DuplicateGSTException("Invalid GST number");
        }
        Optional.ofNullable(updateProfileCO.getGST()).ifPresent(seller::setGST);
        Optional.ofNullable(updateProfileCO.getCompanyContact()).ifPresent(seller::setCompanyContact);
        Optional.ofNullable(updateProfileCO.getCompanyName()).ifPresent(seller::setCompanyName);

        sellerRepository.save(seller);
        return "Profile updated successfully.";
    }

    public String updatePassword(HttpServletRequest request, UpdatePasswordCO updatePasswordCO){
        // check if password and confirm password are not different
        if(!updatePasswordCO.getPassword().equals(updatePasswordCO.getConfirmPassword())){
            throw new ConfirmPasswordMismatchException("Password-Confirm password mismatch");
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

        sellerRepository.save(seller);

        return "Your password has been successfully updated";
    }

    public String updateAddress(HttpServletRequest request, UUID addressId, UpdateAddressCO updateAddressCO){
        //is addressId existing
        if(addressRepository.findById(addressId).isEmpty()){
            return "Address not found";
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

        return "Seller address has been updated successfully";
    }
}
