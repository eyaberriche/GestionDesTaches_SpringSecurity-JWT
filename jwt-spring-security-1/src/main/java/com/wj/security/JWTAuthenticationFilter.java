package com.wj.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wj.dao.entities.AppUser;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}





	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		/*
		 * Si les données sont envoyés en formations JSON; On va passer par Object Mapper
		 * de Jackson pour deserialiser.
		 * Dans mon Objet request je recupère le contenu de la requete, les données JSON; 
		 * Je l'ai stock ensuite dans AppUser
		 * L'objet ObjectMapper permet de prendre des objets JSON 
		 * et de les stocker dans un objet Java
		 */ 
		
		AppUser appUser = null;
		
		/*
		 * => Il récupère les données de l'utilisateur : 
		 * 		Le fait d'instancier ici ObjectMapper avec new n'est pas une bonne 
		 * 		solution car pour chaque tentantive d'authentification, 
		 * 		on crée un objet.
		 * 		request.getInputStream() est le contenu de la requête
		 * 		AppUser.class : on le deserialise dans un objet de type AppUser.
		 * 		Ici on utilise ObjectMapper car les données sont en format Json.
		 */
		try {
			appUser = new ObjectMapper().readValue(request.getInputStream(), AppUser.class);
		} catch (Exception e) {
			/*
			 * S'il y un catch il faut quand même générer une exception afin 
			 * que l'utilisateur réçoit quelque chose
			 * Il prend le même message que l'exception ->  (e)
			 */
			
			throw new RuntimeException(e);
		}
		
		System.out.println("**********************");
		System.out.println("username:" + appUser.getUsername());
		System.out.println("password:" + appUser.getPassword());
		
		// => On returne à Spring Security l'objet authenticationManager.authenticate
		return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		
		/*
		 * authResult va nous permettre de récuprer l'utilisateur qui s'est authentifié
         * getPrincipal nous retourne le nom de l'utilisateur authentitié
		 * getAuthorities nous retourne les roles de l'utilisateur
		 */
		User springUser = (User) authResult.getPrincipal();
		String jwt = Jwts.builder()
				.setSubject(springUser.getUsername())  
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET)
				.claim("roles", springUser.getAuthorities())
				.compact();
		
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt);
	
	}
	
	

}
