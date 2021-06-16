package com.appsdeveloperblog.photoapp.api.users.ui.controllers;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.photoapp.api.users.data.UserEntity;
import com.appsdeveloperblog.photoapp.api.users.service.UsersService;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.UserResponseModel;

@RestController
@RequestMapping("/users")
public class UsersControllers {

    @Autowired
	private Environment env;

    @Autowired
    UsersService   usersservice;
	
	@GetMapping("/status/check")
	public String status() {
		return "workingon port number " + env.getProperty("local.server.port") + "with token =" +env.getProperty("token.secret");
	}
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE}, 
               produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
	
	public ResponseEntity<CreateUserResponseModel> createUser (@Valid @RequestBody CreateUserRequestModel userDetails) {
		

		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserDto userDto = mapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = usersservice.createUser(userDto);
		
		CreateUserResponseModel returnValue = mapper.map(createdUser, CreateUserResponseModel.class);
			
		
		return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}
	
	@GetMapping(value="/{userId}", produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE}) 
	public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId){
		
		UserDto userDto = usersservice.getUserByUserId(userId);
		
		UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.OK).body(returnValue);
		
	}
	
	
}
