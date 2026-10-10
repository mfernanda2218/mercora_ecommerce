package com.mercora.ecommerce.addresses.controller;

import com.mercora.ecommerce.addresses.entity.Address;
import com.mercora.ecommerce.addresses.repository.AddressRepository;
import com.mercora.ecommerce.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    @GetMapping
    public List<Address> getUserAddresses() {
        return addressRepository.findByUserId(getAuthenticatedUser().getId());
    }

    @PostMapping
    public Address createAddress(@RequestBody Address address) {
        address.setUser(getAuthenticatedUser());
        return addressRepository.save(address);
    }
}
