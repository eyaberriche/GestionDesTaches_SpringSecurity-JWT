package com.wj.service;

import com.wj.dao.entities.AppRole;
import com.wj.dao.entities.AppUser;

/*
 * On va créer une une interface dans la couche service 
 * dans laquelle je vais centraliser la gestion des utilisateur et les 
 * rôle. On va gérer les comptes des utilisateurs.
 */
public interface AccountService {
	
	//Methode permettant d'ajouter un utilisateur dans notre Application
	public AppUser saveUser(AppUser user);
	
	//Methode permettant d'ajouter un role dans notre Application
	public AppRole saveRole(AppRole role);
	
	//Methode permettant d'ajouter un rôle à un utilisateur
	public void addRoleToUser(String username, String roleName);
	
	//Methode permettant de retourner un utilisateur
	public AppUser findUserByUserName(String username);
	
}
