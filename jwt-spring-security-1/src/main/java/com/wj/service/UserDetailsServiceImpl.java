package com.wj.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wj.dao.entities.AppUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private AccountService accountService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		/*
		 * On va retourner à Spring Security un objet de type User
		 * Mais un objet User de Spring.
		 * Quand Spring security demande à cette classe cherche l'utilisateur
		 * Cette classe va demander à Account Service donne moi l'utilisateur
		 */
		AppUser user = accountService.findUserByUserName(username);
		
		if(user==null) 
			throw new UsernameNotFoundException(username);
		
		//les rôles pour Spring Security sont des objets d'une Collection de type GrantedAuthority
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(r -> {
			authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
		});
	
		/*
		 * On va  retourner l'objet de l'utilisateur mais l'objet d'une classe User 
		 * qui est un objet de Spring security. 
		 * Je lui donne en parametre le username, le password que je recupère à partir
		 * de AppUser user = accountService.findUserByUserName(username);
		 * je lui donne aussi les authorités, les rôles. Sauf que les rôles pour Spring
		 * Security sont des objets d'une Collection de type GrantedAuthority.
		 * On va  d'abord crée une collection de type ArrayList de GrantedAuthority.
		 * Je vais parcourir les rôles de l'utilisateur; 
		 * Et chaque role de mon utilisateur, je vais l'ajouter dans la liste des authorités
		 * avec un objet de type new SimpleGrantedAuthority, en lui donnant le nom du rôle. 
		 */
		return new User(user.getUsername(), user.getPassword(), authorities);
		
	}

}
