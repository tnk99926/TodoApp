package com.codestep.TodoApp.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.codestep.TodoApp.entities.TodoItem;
import com.codestep.TodoApp.services.TodoService;

@Controller
public class TodoController {
	
	@Autowired
	TodoService todoService;
	
	@Autowired
	UserDetailsManager userDetailsManager;
	
	
	@GetMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView showAddForm(ModelAndView mav, @ModelAttribute TodoItem item) {
		mav.addObject("item", item);
		mav.setViewName("/add");
		return mav;
	}
	
	@PostMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView PostAddForm(ModelAndView mav, @ModelAttribute("item") @Validated TodoItem item, BindingResult result) {
		if (result.hasErrors()) {
			mav.addObject("item", item);
			mav.setViewName("/add");
			return mav;
		}
		
		String name = SecurityContextHolder.getContext().getAuthentication().getName(); 
		todoService.create(name, item);
		mav.addObject("item", item);
		mav.setViewName("redirect:/add");
		return mav;
	}
	
	record TodoItemEx(TodoItem item, String strDone, boolean caution, boolean overdue) {
	};
	
	@GetMapping("/list")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView showList(ModelAndView mav) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		var user = userDetailsManager.loadUserByUsername(username);
		List<TodoItem> list;
		if (user.getAuthorities().stream().allMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"))) {
			list = todoService.findAll();
		} else {
			list = todoService.findByUserName(username);
		}
		
		LocalDate now = LocalDate.now();
		
		LocalDate notifyDt = now.plusDays(7);
		
		List<TodoItemEx> listEx = new ArrayList<>();
		
		for(TodoItem todoItem : list) {
			String strDone = todoService.getStrDone(todoItem);
			
			LocalDate deadline = todoItem.getDeadline();
			TodoItemEx todoItemEx;
			if (deadline == null) {
				todoItemEx = new TodoItemEx(todoItem, strDone, false, false);//期日未設定
			} else if(todoItem.getDone() == 2) {
				todoItemEx = new TodoItemEx(todoItem, strDone, false, false);//完了済み
			} else if(deadline.isBefore(now)){
				todoItemEx = new TodoItemEx(todoItem, strDone, true, true);//期日超過
			} else{
				todoItemEx = new TodoItemEx(todoItem, strDone, deadline.isBefore(notifyDt), false);//期日7日前とそれ以外をisBeforeで判定
			}
			
			listEx.add(todoItemEx);
		}
		
		mav.addObject("loginUserName",username);
		mav.addObject("role",user.getAuthorities());
		mav.addObject("listEx",listEx);
		mav.setViewName("list");
		return mav;
	}
	
	@GetMapping("/item/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView showItem(ModelAndView mav, @PathVariable int id) {
		TodoItem todoItem = todoService.getById(id);
		LocalDate now = LocalDate.now();
		
		LocalDate notifyDt = now.plusDays(7);
		
		String strDone = todoService.getStrDone(todoItem);
		
		LocalDate deadline = todoItem.getDeadline();
		TodoItemEx todoItemEx;
		if (deadline == null) {
			todoItemEx = new TodoItemEx(todoItem, strDone, false, false);//期日未設定
		} else if(todoItem.getDone() == 2) {
			todoItemEx = new TodoItemEx(todoItem, strDone, false, false);//完了済み
		} else if(deadline.isBefore(now)){
			todoItemEx = new TodoItemEx(todoItem, strDone, true, true);//期日超過
		} else {
			todoItemEx = new TodoItemEx(todoItem, strDone, deadline.isBefore(notifyDt), false);//期日7日前とそれ以外をisBeforeで判定
		}
		
		mav.addObject("itemEx",todoItemEx);
		mav.setViewName("item");
		return mav;
	}
}
