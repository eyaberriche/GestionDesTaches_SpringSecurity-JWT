package com.wj.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wj.dao.TaskRepository;
import com.wj.dao.entities.Task;

@RestController
public class TaskRestController {
	
	
	@Autowired
	TaskRepository taskRepository;
	
	//Remarque: On peut faire l'injection via un constructeur avec parametre. 
	//Spring quand il va instancier cette classe, automatiquement il va injecter taskRespository
	/*public TaskRestController(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}*/
	
	
	@GetMapping("/tasks")
	public List<Task> getlistTasks() {
		return taskRepository.findAll();
	}
	
	
	@PostMapping("/tasks")
	/*
	 * RequestBody : c'est la notation qui va être indiquer à Spring 
	 * afin qu'il puisse chercher le contenu, le corps de la requête
	 * et le mettre dans l'objet Task
	 */
	public Task saveTask(@RequestBody Task task) {
		return taskRepository.save(task);
	}
	
}
