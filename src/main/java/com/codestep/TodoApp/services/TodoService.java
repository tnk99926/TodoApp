package com.codestep.TodoApp.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codestep.TodoApp.Repositories.TodoRepository;
import com.codestep.TodoApp.entities.TodoItem;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TodoService {
	
	@Autowired
	TodoRepository todoRepository;
	
	public TodoItem create(String username, TodoItem item) {
		item.setUserName(username);
		item.setCreated(LocalDate.now());
		item.setDone(0);
		todoRepository.saveAndFlush(item);
		return item;
	}
}
