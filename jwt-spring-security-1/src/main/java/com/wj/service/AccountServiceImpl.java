package com.wj.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wj.dao.RoleRepository;
import com.wj.dao.UserRepository;
import com.wj.dao.entities.AppRole;
import com.wj.dao.entities.AppUser;

/*
 * Pour que Spring puisse instancier cette classe au démarrage il faut 
 * utiliser l'annotation Service. Cette annotation est utilisée pour les
 * objets de la couche métier.
 * On peut utiliser aussi l'annotation Transactional pour demander à Spring
 * de gerer les transaction. 
 * Donc toutes les méthodes seront transactionnelle. 
 * Ainsi, on assure que les méthodes s'éxecutent correctement 
 * Sinon on annule tout
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService{

	/*
	 * Comme c'est déjà un Bean Spring 
	 * On peut utiliser Autowired pour BCryptPasswordEncoder
	 * Car on l'a déjà instancié au démarrage de l'application
	 * Pourquoi a t-on besoin de ça? 
	 * Quand l'utilisateur veut s'enregistrer il m'envoie son mot de passe
	 * Mais avant de l'enregistrer dans la base de données, je dois crypter le mot 
	 * de passe.
	 */
	@Autowired 
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@Override
	public AppUser saveUser(AppUser user) {
		
		/*
		 * On encode le mot de passe qui a été saisie par l'utilisateur
		 * avant de l'ajouter à la base de données.
		 */
		String hashPW= bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(hashPW);
		return userRepository.save(user);
		
	}

	@Override
	public AppRole saveRole(AppRole role) {
		
		return roleRepository.save(role);
		
	}

	//rolename est le nom de l'utilisateur qu'il faut ajouter le rôle
	@Override
	public void addRoleToUser(String username, String roleName) {
		
		//On recupère le rôle, et l'utilisateur
		AppRole role = roleRepository.findByRoleName(roleName);
		AppUser user = userRepository.findByUsername(username);
		
		/*
		 * On accède à la liste des role de l'utilisateur afin de lui ajouter 
		 * le role.
		 * Comme la methode est transactionnel: 
		 * Dès qu'il fait commit; automatiquement il sait qu'on ajouté un rôle 
		 * et automatiquement il l'ajoute au niveau de la base de données
		 */
		user.getRoles().add(role);
		
	}

	@Override
	public AppUser findUserByUserName(String username) {
		return userRepository.findByUsername(username);
	}

}
