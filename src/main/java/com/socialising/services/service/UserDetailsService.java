package com.socialising.services.service;

import com.socialising.services.model.User;
import com.socialising.services.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNo);

        if(user == null) {
            user = new User();
            user.setPhoneNumber(phoneNo);
            userRepository.save(user);
        }

        return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), "", new ArrayList<>());
    }
}
