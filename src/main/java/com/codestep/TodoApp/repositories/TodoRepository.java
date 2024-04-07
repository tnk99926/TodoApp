package com.codestep.TodoApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codestep.TodoApp.entities.TodoItem;

public interface TodoRepository extends JpaRepository<TodoItem, Long>{
	public List<TodoItem> findByUserName(String username);
}
