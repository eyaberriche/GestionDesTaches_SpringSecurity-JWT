package com.wj.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity		//Active la securité Web
public class SecurityConfig  extends WebSecurityConfigurerAdapter{

	/*
	 * On va demander à Spring d'utiliser un Service
	 * On va utiliser un système d'authentification basé sur une couche service
	 * Dans notre application on va créer une implementation cette interface
	 */
	@Autowired
	private UserDetailsService  userDetailsService;
	
	
	/*
	 * Il faudra instancier cette interface car ce n'est pas un Bean qu'il 
	 * instancie par défaut
	 */
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		/*
		 * On définie comment Spring Security va aller chercher les utilisateurs
		 * et les rôles.
		 * Si on utilise inMemoryAuthentication et jdbcAuthentication(), 
		 * ce n'est pas la peine de créer la classe role et user pour gerer les
		 * utilisateur et les rôles
		 */
	
		/*	auth.inMemoryAuthentication().withUser("admin").password("{noop}1234").roles("ADMIN", "USER")
			.and()
			.withUser("user").password("{noop}1234").roles("USER");	 
		*/
		
		
		/*  Avant on faisait une authentification basée sur inMemoryAuthentication() ou jdbcAuthentication()
		 * 	Maintant on fait une authentification basée sur UserDetails
		 * 	userDetailsService va gérer les utilisateurs et les rôles.
		 *  Pour un utilisateur qui tente de s'autentifier Spring security va recuperer les 
            informations de l'utilisateur, s'il existe, en utilisant la 
            methode loadUserByUsername de la classe userDerailsService
		 * 	userDaitailsService va récuperer le user (username, password, roles) dans la base de données
		 * 	Spring security compare le mot de passe de l'utilisateur avec le mot de passe recupéré
		 * 	dans la base.
		 * 	On précise quel type d'encodage qu'on va utiliser passwordEncoder(bCryptPasswordEncoder)
		 *	Quand Spring Security va tenter de verifier le mot de passe, il va Hacher, encoder
		 *	le mot de passe saisie par l'utilisateur avec bCrypt et le comparer avec le
		 *	mot de passe qui est dans la base de données. Dans la base de données
		 *	le mot de passe est déjà encodé en bCrypt
		 */
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(bCryptPasswordEncoder);
		
	}
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * On définie les droits d'accès, telle route neccéssite d'être 
		 * authetifié avec quel utilisateur.
		 * On définie aussi les filtres
		 */
	
		/*
		 * Désactiver le Syncronize Token: On dit à Spring Security 
		 * Ce n'est plus la peine de générer le Syncronize Token (Utile contre les attaques CSRF)
		 */
		http.csrf().disable();
		
		/*
		 * On a dit à Spring security c'est plus la peine de créer les sessions; 
		 * on desactive donc les sessions
		 * Il ne gardera pas la session de l'utilisateur en mémoire.
		 * On va passer donc d'un systeme d'authentification par valeur avec JWT
		 * Car tout va se trouver dans le Token
		 */
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		//http.formLogin(); //on a plus besoins si on veut utiliser JWT
		/*
		 * register permet d'enregistrer un nouveau utilisateur.
		 * Quand on veut accéder au login il ne faut pas qu'il neccéssite 
		 * une authentification, sinon on ne pourra jamais s'authentifier; 
		 * Dans ce cas il faut ajouter la méthode permitAll().
		 * Pareil pour register; on a pas besoin de s'authentifier pour
		 * s'enregistrer
		 */
		http.authorizeRequests().antMatchers("/login/**", "/register/**").permitAll();
		
		/*
		 * Ici, si une requête est envoyée avec post vers /tasks
		 * Il faut l'autoriser que si l'utilisateur a le rôle ADMIN
		 */
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/tasks/**").hasAuthority("ADMIN");
		
		http.authorizeRequests().anyRequest().authenticated();
		
		//On ajoute les filtres 
		//authenticationManager():Méthode qui herite de WebSecurityConfigurerAdapter
		//C'est lui qui gère l'authentification
		http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
		
		/*
		 * Utilisation du filtre JWTAuthorisationFilter
		 */
		http.addFilterBefore(new JWTAuthorisationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	
}
