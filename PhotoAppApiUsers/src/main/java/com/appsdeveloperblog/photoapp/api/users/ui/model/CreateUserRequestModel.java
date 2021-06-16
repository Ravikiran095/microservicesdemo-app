package com.appsdeveloperblog.photoapp.api.users.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {

    @NotNull(message= "First name cannot be null")
    @Size(min =2, message = "name should not be less than 2 char")
	private String firstname;

    @NotNull(message= "lastname cannot be null")
    @Size(min =2, message = "name should not be less than 2 char")
	private String lastname;

    @NotNull(message= " password cannot be null")
    @Size(min =2,max = 16, message = "pwd should not be null")
	private String password;
    

    @NotNull(message= "email  cannot be null")
    @Email
     private String email;
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
