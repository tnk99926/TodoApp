package com.codestep.TodoApp.Controllers;

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
}
