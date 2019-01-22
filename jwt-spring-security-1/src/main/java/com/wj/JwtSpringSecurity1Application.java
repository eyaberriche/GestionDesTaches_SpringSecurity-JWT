package com.wj;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wj.dao.TaskRepository;
import com.wj.dao.entities.AppRole;
import com.wj.dao.entities.AppUser;
import com.wj.dao.entities.Task;
import com.wj.service.AccountService;

@SpringBootApplication
public class JwtSpringSecurity1Application implements CommandLineRunner{

	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(JwtSpringSecurity1Application.class, args);
	}
	
	/*
	 * Quand l'application va démarrer toutes les méthodes qui utilise
	 * l'annotation Bean sont executées. Le resultat retourné devient un
	 * Bean Spring. Quand ça devient un Bean Spring. On peut l'injecter
	 * partout
	 */
	@Bean 
	public BCryptPasswordEncoder getBCPE() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		//Ajout des utilisateurs
		accountService.saveUser(new AppUser(null, "wood", "1234", null));
		accountService.saveUser(new AppUser(null, "louis", "1234", null));
		
		//Ajout des rôles
		accountService.saveRole(new AppRole(null, "ADMIN"));
		accountService.saveRole(new AppRole(null, "USER"));
		
		//Ajout des rôles aux utilisateurs
		accountService.addRoleToUser("wood", "ADMIN");
		accountService.addRoleToUser("wood", "USER");
		accountService.addRoleToUser("louis", "USER");

		
		//Ajout des tâches
		Stream.of("T1", "T2", "T3").forEach(t -> {
			taskRepository.save(new Task(null, t));
		});
		taskRepository.findAll().forEach(t-> {
			System.out.println(t);
			});
	}
}

