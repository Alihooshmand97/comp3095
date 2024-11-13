package com.example.userservice;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.model.UserType;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void setup() {
		userRepository.deleteAll();
	}

	@Test
	public void testCreateUser() {
		UserRequest userRequest = new UserRequest();
		userRequest.setName("John Doe");
		userRequest.setEmail("john.doe@example.com");
		userRequest.setRole(Role.ADMIN);
		userRequest.setUserType(UserType.FACULTY);

		UserResponse userResponse = userService.createUser(userRequest);

		assertThat(userResponse).isNotNull();
		assertThat(userResponse.getName()).isEqualTo("John Doe");
		assertThat(userResponse.getEmail()).isEqualTo("john.doe@example.com");
	}

	@Test
	public void testGetUserById() {
		UserRequest userRequest = new UserRequest();
		userRequest.setName("Jane Doe");
		userRequest.setEmail("jane.doe@example.com");
		userRequest.setRole(Role.ORGANIZER);
		userRequest.setUserType(UserType.STAFF);

		UserResponse createdUser = userService.createUser(userRequest);
		Long userId = createdUser.getId();

		UserResponse retrievedUser = userService.getUserById(userId);

		assertThat(retrievedUser).isNotNull();
		assertThat(retrievedUser.getId()).isEqualTo(userId);
		assertThat(retrievedUser.getName()).isEqualTo("Jane Doe");
	}

	@Test
	public void testDeleteUser() {
		UserRequest userRequest = new UserRequest();
		userRequest.setName("To Be Deleted");
		userRequest.setEmail("delete@example.com");
		userRequest.setRole(Role.ADMIN);
		userRequest.setUserType(UserType.FACULTY);

		UserResponse createdUser = userService.createUser(userRequest);
		Long userId = createdUser.getId();

		userService.deleteUser(userId);
		assertThat(userRepository.existsById(userId)).isFalse();
	}
}
