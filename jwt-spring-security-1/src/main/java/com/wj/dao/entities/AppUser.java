package com.wj.dao.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
/*@Data @AllArgsConstructor @NoArgsConstructor
*/
public class AppUser {
	
	@Id @GeneratedValue
	private Long id;
	//Le username doit être unique; donc on utilise l'annotation @Column(unique=true)
	@Column(unique=true)
	private String username;
	
	/*
	 * Il ne faut jamais retourner le password à l'utilisateur.
	 * Pour cela on peut ajouter jsonIgnore.
	 * Lors de la serialisation de l'objet AppUser par Jackson, 
	 * cette donnée ne sera pas pris en compte
	 * Normalement on le fait sur les getter et setter.
	 * Le soucis c'est qu'il ignore le mot de passe dans la deserialisation.
	 * On a donc un NullPointerException
	 * On va donc mettre JsonIgnore sur getPassword()
	 * Et JsonSetter sur setPassword()
	 * Cela veut dire qu'on l'ignorera lors de la Serialisation et non lors
	 * de la deserialisation
	 */
	//@JsonIgnore
	private String password;
	
	//EAGER: A chaque fois je vais charger l'utilisateur; automatiquement je vais charger ses roles
	@ManyToMany(fetch=FetchType.EAGER)
	private Collection<AppRole> roles = new ArrayList<>();

	
	public AppUser() {
		super();
	}

	public AppUser(Long id, String username, String password, Collection<AppRole> roles) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	
	
	@JsonSetter
	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<AppRole> getRoles() {
		return roles;
	}

	public void setRoles(Collection<AppRole> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "AppUser [id=" + id + ", username=" + username + ", password=" + password + ", roles=" + roles + "]";
	}


}
