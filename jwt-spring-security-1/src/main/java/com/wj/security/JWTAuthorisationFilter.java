package com.wj.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/*
 * C'est un filtre qui va intervenir pour chaque requête
 * Chaque requête qui arrive, il va verifier s'il contient ce token 
 */
public class JWTAuthorisationFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		/*
		 * Permet de régler le problème  de CORS
		 * Ici j'autorise tous les domaine à m'envoyer des requêtes
		 */
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		/*
		 * Permet de regler le problème d'autorisation d'entête de type : 
		 * Request header field content-type is not allowed by Access-Control-Allow-Headers
		 * in preflight response
		 * Il faut donner à Angular l'autorisation d'utiliser certains entêtes.
		 * C'est Access-Control-Allow-Headers qui donne l'autorisation.
		 * Ici on va lui envoyer par exemple les entêtes Content-Type,
		 * Accept, autorization(contenant le jwt)
		 */
		response.addHeader("Access-Control-Allow-Headers", 
				"Origin, Accept, X-Request-Width, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, authorization");
		
		
		/* Dans la partie front end on a l'entête autorization qu'on veut recupérer 
		 * qui est égale à  nul. Comment le recupérer ?
		 * Access-Control-Expose-Headers permet à la partie front-end de lire
		 * certains entêtes 
		 */
		response.addHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, authorization");
		
		
		/*
		 * On va regarder dans l'objet request s'il contient 
		 * un header qui s'appelle Authorization.
		 * Recupération du token jwt
		 */
		String jwt = request.getHeader(SecurityConstants.HEADER_STRING);
		System.out.println(jwt);
		
		
		/*
		 * On dit au filtre que Si la méthode est OPTIONS,
		 * ce n'est pas la peine d'utiliser les Règle de sécurité.
		 * On utilise SC_OK (status code = OK).
		 * Sinon la requête avec OPTIONS ne sera pas acceptée
		 * car Options ne contient pas le Token
		 */
		if(request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else {
			
			//si jwt est null et ne commence pas par le prefix Bearer
			if(jwt == null || !jwt.startsWith(SecurityConstants.TOKEN_PREFIX)) {
				/*
				 * doFilter veut dire qu'il passe au filtre suivant.
				 * On va vers Spring Security
				 */
				filterChain.doFilter(request, response);
				return;  //Je qui la méthode
			}
			
			/*
			 * Récuperation des Claims, les informations
			 * parser() : On decode le JWT
			 */
			Claims claims = Jwts.parser()
				.setSigningKey(SecurityConstants.SECRET)  //On signe avec le même secret
				.parseClaimsJws(jwt.replace(SecurityConstants.TOKEN_PREFIX, "")) // On enlève le préfixe
				.getBody();
			
			
			String username = claims.getSubject(); //Il recupère le sujet
			
			/*
			 * Recupération des rôles qui contient une  clé authority et une valeur
			 * ADMIN, USER
			 * Chaque Element Arraylist est un Map 
			 * Les rôles pour Spring Security sont des objets d'une Collection de type GrantedAuthority.
			 * On va  d'abord crée une collection de type ArrayList de GrantedAuthority.
			 * Je vais parcourir les rôles de l'utilisateur; 
			 * Et chaque role de mon utilisateur, je vais l'ajouter dans la liste des authorités
			 * avec un objet de type new SimpleGrantedAuthority, en lui donnant le nom du rôle. 
			 */
			ArrayList<Map<String, String>> roles = (ArrayList<Map<String,String>>) 
					claims.get("roles"); 
			Collection<GrantedAuthority> authorities  = new ArrayList<>();
			roles.forEach(r -> {
				authorities.add(new SimpleGrantedAuthority(r.get("authority"))); //authority est la clé
			});
			
			
			/*
			 * 	Je crée un Objet de type UsernamePasswordAuthenticationToken  afin de le retourner 
			 *  à Spring Security. Je transmet le username et authorities(car il a besoin de connaitre
			 *  les roles de l'utilisateur). Spring security  a besoin des rôles pour l'accès aux routes.
			 *  Le mot de passe on en a pas besoin car elle n'existe pas dans le Token
			 */
			UsernamePasswordAuthenticationToken  authenticatedUser = 
					new UsernamePasswordAuthenticationToken(username, null, authorities);
			
			
			/*
			 * On charge ensuite  l'utilisateur authentifié dans le contexte de Spring Security.
			 * Je dis à Spring security, l'utilisateur qui a envoyé la requête, voila son identité.
			 * Ainsi, il va connaitre le username et les roles de l'utilisateur.
			 */
			SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
			filterChain.doFilter(request, response);		
			
		}
		
	}
}
