package com.codestep.TodoApp.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
	
	@GetMapping("/user/add")
	@PreAuthorize("permitAll")
	public ModelAndView showAddForm(ModelAndView mav) {
		mav.setViewName("/user/add");
		return mav;
	}
	
	@Autowired
	UserDetailsManager userDetailsManager;
	
	@PostMapping("/user/add")
	@PreAuthorize("permitAll")
	public ModelAndView postAddForm(@RequestParam String username, @RequestParam String password, @RequestParam String role, ModelAndView mav) {
		var user = User
				.withUsername(username)
				.password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password))
				.roles(role)
				.build();
		userDetailsManager.createUser(user);
		mav.setViewName("/user/add");
		return mav;
	}
	
}
