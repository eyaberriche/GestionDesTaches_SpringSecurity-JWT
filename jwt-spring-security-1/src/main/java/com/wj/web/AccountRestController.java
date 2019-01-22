package com.wj.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wj.dao.entities.AppUser;
import com.wj.service.AccountService;

@RestController
public class AccountRestController {
	
	@Autowired
	private AccountService accountService;
	
	
	@PostMapping("/register")
	/*
	 * On indique à Spring de récuperer le corps de la requête 
	 * 	et de le mettre dans  l'objet RegisterForm
	 */
	public AppUser registerUser(@RequestBody RegisterForm userForm) {
		
		if(!userForm.getPassword().equals(userForm.getRepassword()))
			throw new RuntimeException("You must confirm your password");
		
		//On verifie si cette utilisateur existe déjà dans la base de données
		AppUser user = accountService.findUserByUserName(userForm.getUsername());
		if(user != null) throw new RuntimeException("This user already exists");
		
		//Si l'utilisateur n'existe pas on va le créer
		AppUser appUser = new AppUser();
		appUser.setUsername(userForm.getUsername());
		appUser.setPassword(userForm.getPassword());
		accountService.saveUser(appUser);
		//Une fois l'utilisateur enregistré, je lui donne un rôle par défaut
		accountService.addRoleToUser(userForm.getUsername(), "USER"); 
		
		return appUser;
		
	}
	
}
