package com.codestep.TodoApp.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.codestep.TodoApp.entities.TodoItem;
import com.codestep.TodoApp.repositories.TodoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TodoService {
	
	@Autowired
	TodoRepository todoRepository;
	final int WAITING = 0;
	final int IN_PROGRESS = 1;
	final int DONE = 2;
	
	public TodoItem create(String username, TodoItem item) {
		item.setUserName(username);
		item.setCreated(LocalDate.now());
		item.setDone(WAITING);
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
		 return switch(DONE_STATUS) {
			case 1 -> "着手中";
			case 2 -> "完了";
			default ->"未着手";
		 };
	}
	
	public void complete(long id, boolean inProgress) {
		TodoItem item = todoRepository.getReferenceById(id);
		if(inProgress) {
			item.setDone(IN_PROGRESS);
			item.setCompletion(null);
		} else {
			item.setDone(DONE);
			item.setCompletion(LocalDate.now());
		}
	}
	
	public void delete (long id) {
		todoRepository.deleteById(id);
	}
	
	@Autowired
	UserDetailsManager userDetailsManager;
	
	public boolean isLoginUserOrAdmin(long id) {
		TodoItem todoItem = this.getById(id);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		var user = userDetailsManager.loadUserByUsername(username);
		
		boolean isLoginUser = todoItem.getUserName().equals(user.getUsername());
		boolean isRoleAdmin = user.getAuthorities().stream().allMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
		if(isLoginUser || isRoleAdmin) {
			return true;
		} else {
			return false;
		}
	}
	
	public void update(long id, String title, LocalDate deadline) {
		TodoItem item = todoRepository.getReferenceById(id);
		item.setTitle(title);
		item.setDeadline(deadline);
	}
}
