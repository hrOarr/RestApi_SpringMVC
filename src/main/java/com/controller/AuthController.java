package com.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.model.User;
import com.service.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	private UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@ApiOperation(value = "Login API")
	@PostMapping("/login")
	public ResponseEntity<?> submitLogin(@Valid @RequestBody User user, BindingResult result) {
		
		if(result.hasFieldErrors("email")||result.hasFieldErrors("password")) {
			List<String> errors = result.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		// check user password
		User obj = userService.getUserByEmail(user.getEmail());
		if(obj==null) {
			return ResponseEntity.badRequest().body("Email is incorrect!");
		}
		else if(!obj.getPassword().equals(user.getPassword())) {
			return ResponseEntity.badRequest().body("Password is incorrect!");
		}
		return ResponseEntity.ok(obj);
	}
	
	@ApiOperation(value = "Register API")
	@PostMapping("/register")
	public ResponseEntity<?> submitRegister(@Valid @RequestBody User user, BindingResult result) {
		if(result.hasErrors()) {
			List<String> errors = result.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		// check if already exist or not
		User obj = userService.getUserByEmail(user.getEmail());
		if(obj!=null) {
			return ResponseEntity.badRequest().body("Email is already in use!");
		}
		userService.saveUser(user);
		return ResponseEntity.ok(userService.getUserByEmail(user.getEmail()));
	}
}
