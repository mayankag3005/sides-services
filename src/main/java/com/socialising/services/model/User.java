package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = { "username", "userId", "phoneNumber" }) })
@Entity
@Data
public class User {

    public Long getUserId() {
        return userId;
    }

    public void setUserId() {
        this.userId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
    }

    public String getUsername() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setPhoneNumber(String phoneNo) {
        this.phoneNumber = phoneNo;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Long[] getFriends() {
        return friends;
    }

    public void setFriends(Long[] friends) {
        this.friends = friends;
    }

    public Long[] getPosts() {
        return posts;
    }

    public void setPosts(Long[] posts) {
        this.posts = posts;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getEducation() {
        return education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getReligion() {
        return religion;
    }

    public Long[] getReminderPosts() {
        return reminderPosts;
    }

    public void setReminderPosts(Long[] reminderPosts) {
        this.reminderPosts = reminderPosts;
    }

    public Long[] getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(Long[] friendRequests) {
        this.friendRequests = friendRequests;
    }

    @Id
    @Column(unique=true)
    private Long userId;

    @Column(unique=true)
    private String username;

    private String firstName;

    private String lastName;

    private String email;

    @Column(unique=true)
    private String phoneNumber;

    private String dob;

    private Integer age;

    private String gender;

    private String religion;

    private String maritalStatus;

    private String city;

    private String state;

    private String country;

    private String education;

    private String occupation;

    private Long[] friendRequests;

    private Long[] friends;

    private Long[] posts;

    private String[] tags;

    private Long[] reminderPosts;
}
