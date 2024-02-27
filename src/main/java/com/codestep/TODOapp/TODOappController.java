package com.codestep.TODOapp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class TODOappController {
	
	@RequestMapping("/")
	@PreAuthorize("permitAll")
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName("index");
		mav.addObject("title", "index page");
		mav.addObject("msg", "This is top page.");
		return mav;
	}
	
	@RequestMapping("/secret")
	@PreAuthorize("hasRole('ADMIN')")
	public ModelAndView secret(ModelAndView mav, HttpServletRequest request) {
		String user = request.getRemoteUser();
		String msg = "This is secret page. [login by \"" + user + "\"]";
		mav.setViewName("Secret");
		mav.addObject("title", "Secret page");
		mav.addObject("msg", msg);
		return mav;
	}
}
