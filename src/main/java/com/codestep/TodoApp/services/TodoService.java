package com.codestep.TodoApp.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codestep.TodoApp.entities.TodoItem;
import com.codestep.TodoApp.repositories.TodoRepository;

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
	
	public List<TodoItem> findAll(){
		return todoRepository.findAll();
	}
	
	public List<TodoItem> findByUserName(String username) {
		return todoRepository.findByUserName(username);
	}
	
	public TodoItem getById(long id) {
		return todoRepository.getReferenceById(id);
	}
	
	public String getStrDone(TodoItem todoItem) {
		final int DONE_STATUS = todoItem.getDone();
		switch(DONE_STATUS) {
		case 1 ->{
			return "着手中";
		}
		case 2 ->{
			return  "完了";
		}
		default ->{
			return  "未着手";
		}
	}
	}
}
