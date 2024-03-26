package com.codestep.TodoApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codestep.TodoApp.entities.TodoItem;

public interface TodoRepository extends JpaRepository<TodoItem, Long>{

}
