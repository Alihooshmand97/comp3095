package com.example.userservice.dto;

import com.example.userservice.model.Role;
import com.example.userservice.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String name;
    private String email;
    private Role role;
    private UserType userType;
}
