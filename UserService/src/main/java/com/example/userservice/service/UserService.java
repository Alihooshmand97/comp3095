package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.model.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    List<UserResponse> getAllUsers();
    String getUserRoleById(Long id);
    void deleteUser(Long id);

    UserResponse getUserById(Long id);
}
