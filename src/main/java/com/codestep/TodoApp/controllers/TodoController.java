package com.codestep.TodoApp.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.codestep.TodoApp.entities.TodoItem;
import com.codestep.TodoApp.services.TodoService;

@Controller
public class TodoController {
	
	@Autowired
	TodoService todoService;
	
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
	
	record TodoItemEx(TodoItem item, boolean caution, boolean overdue) {
	};
	
	@GetMapping("/list")
	public ModelAndView showList(ModelAndView mav) {
		List<TodoItem> list = todoService.findAll();
		
		LocalDate now = LocalDate.now();
		
		LocalDate notifyDt = now.plusDays(7);
		
		List<TodoItemEx> listEx = new ArrayList<>();
		
		for(TodoItem todoItem : list) {
			LocalDate deadline = todoItem.getDeadline();
			TodoItemEx todoItemEx;
			if (deadline == null) {
				todoItemEx = new TodoItemEx(todoItem, false, false);
			} else if(todoItem.getDone() == 2) {
				todoItemEx = new TodoItemEx(todoItem, false, false);
			} else if(deadline.isBefore(now)){
				todoItemEx = new TodoItemEx(todoItem, true, true);
			} else{
				todoItemEx = new TodoItemEx(todoItem, deadline.isBefore(notifyDt), false);
			}
			listEx.add(todoItemEx);
		}
		
		
		mav.addObject("listEx",listEx);
		mav.setViewName("list");
		return mav;
	}
}
