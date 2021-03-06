package com.appsdeveloperblog.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.appsdeveloperblog.photoapp.api.users.data.AlbumServiceClient;
import com.appsdeveloperblog.photoapp.api.users.data.UserEntity;
import com.appsdeveloperblog.photoapp.api.users.data.UsersRepository;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;
import org.springframework.core.env.Environment;

@Service
public class UsersServiceImplementation implements UsersService {

	UsersRepository usersrepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
//	RestTemplate restTemplate;
	Environment environment;
	AlbumServiceClient albumServiceClient;
	 
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public UsersServiceImplementation( UsersRepository usersrepository, BCryptPasswordEncoder bCryptPasswordEncoder, 
			Environment environment, AlbumServiceClient albumServiceClient) {
		this.usersrepository = usersrepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		//this.restTemplate =  restTemplate;
		this.environment = environment;
	    this.albumServiceClient = albumServiceClient;
	}
	
	@Override
	public UserDto createUser(UserDto userDet) {

		userDet.setUserId(UUID.randomUUID().toString());
		userDet.setEncryptedPassword(bCryptPasswordEncoder.encode(userDet.getPassword()));
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserEntity userEntity = mapper.map(userDet,UserEntity.class);
		usersrepository.save(userEntity);
		
		UserDto returnValue = mapper.map(userEntity,UserDto.class );
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersrepository.findByEmail(username);
		
		if(userEntity == null) throw new UsernameNotFoundException(username);	
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	
	@Override
	public UserDto getUserDetailsByEmail(String email) { 
		UserEntity userEntity = usersrepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
    
		UserEntity userEntity = usersrepository.findByUserId(userId);
		if(userEntity== null) throw new UsernameNotFoundException("User not found");
		
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		
		/*
        String albumsUrl = String.format(environment.getProperty("albums.url"), userId);
        
        ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        });
        List<AlbumResponseModel> albumsList = albumsListResponse.getBody(); 
        */
		
		logger.info("Before calling albums microservice");
		
        List<AlbumResponseModel> albumsList = albumServiceClient.getAlbums(userId) ; 
        
		logger.info("After calling albums microservice");

        
		userDto.setAlbums(albumsList);
		
		return userDto;
		
	}
	
}
