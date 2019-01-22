package com.wj.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wj.dao.entities.AppRole;

public interface RoleRepository extends JpaRepository<AppRole, Long>{
	
	public AppRole findByRoleName(String roleName);
	
}
