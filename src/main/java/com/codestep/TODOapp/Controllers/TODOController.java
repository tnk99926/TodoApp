package com.codestep.TODOapp.Controllers;

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

import com.codestep.TODOapp.TODOItem;
import com.codestep.TODOapp.Repositories.TODOService;

@Controller
public class TODOController {
	
	@Autowired
	TODOService TODOService;
	
	@GetMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView showAddForm(ModelAndView mav, @ModelAttribute TODOItem item) {
		mav.addObject("item", item);
		mav.setViewName("/add");
		return mav;
	}
	
	@PostMapping("/add")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView PostAddForm(ModelAndView mav, @ModelAttribute("item") @Validated TODOItem item, BindingResult result) {
		if (result.hasErrors()) {
			mav.addObject("item", item);
			mav.setViewName("/add");
			return mav;
		}
		
		String name = SecurityContextHolder.getContext().getAuthentication().getName(); 
		TODOService.create(name, item);
		mav.addObject("item", item);
		mav.setViewName("redirect:/add");
		return mav;
	}
}
