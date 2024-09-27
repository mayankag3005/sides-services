package com.socialising.services.mapper;

import com.socialising.services.dto.UserDTO;
import com.socialising.services.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static User dtoToEntity(UserDTO dto) {
        User user = new User();
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDob(dto.getDob());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setReligion(dto.getReligion());
        user.setMaritalStatus(dto.getMaritalStatus());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setHomeCity(dto.getHomeCity());
        user.setHomeState(dto.getHomeState());
        user.setCountry(dto.getCountry());
        user.setEducation(dto.getEducation());
        user.setOccupation(dto.getOccupation());
        user.setTags(dto.getTags() != null ? dto.getTags().toArray(new String[0]) : new String[]{});
        return user;
    }

    public static UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setDob(user.getDob());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setReligion(user.getReligion());
        dto.setMaritalStatus(user.getMaritalStatus());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setHomeCity(user.getHomeCity());
        dto.setHomeState(user.getHomeState());
        dto.setCountry(user.getCountry());
        dto.setEducation(user.getEducation());
        dto.setOccupation(user.getOccupation());
        dto.setTags(user.getTags() != null ? List.of(user.getTags()) : new ArrayList<>());
        return dto;
    }
}

