package com.wj.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wj.dao.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{

}
