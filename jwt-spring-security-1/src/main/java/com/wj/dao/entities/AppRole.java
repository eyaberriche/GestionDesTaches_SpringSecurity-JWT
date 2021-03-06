package com.wj.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class AppRole {
	
	@Id @GeneratedValue
	private Long id;
	
	/*
	 * Pas d'association, car Je n'ai pas besoin pour un role de savoir 
	 * tous les utilisateurs qui ont ce role
	 */
	private String roleName;
	
}
