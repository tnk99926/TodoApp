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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.codestep.TODOapp.TODOItem;
import com.codestep.TODOapp.Repositories.TODOService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class TODOController {
	
	@RequestMapping("/")
	@PreAuthorize("permitAll")
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName("index");
		mav.addObject("title", "index page");
		mav.addObject("msg", "This is top page.");
		return mav;
	}
	
	@RequestMapping("/secret")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView secret(ModelAndView mav, HttpServletRequest request) {
		String user = request.getRemoteUser();
		String msg = "This is secret page. [login by \"" + user + "\"]";
		mav.setViewName("secret");
		mav.addObject("title", "Secret page");
		mav.addObject("msg", msg);
		return mav;
	}
	
	@RequestMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ModelAndView admin(ModelAndView mav, HttpServletRequest request) {
		String user = request.getRemoteUser();
		String msg = "This is admin page. [login by \"" + user + "\"]";
		mav.setViewName("admin");
		mav.addObject("title", "Admin page");
		mav.addObject("msg", msg);
		return mav;
	}
	
	@RequestMapping("/login")
	@PreAuthorize("permitAll")
	public ModelAndView login(ModelAndView mav, @RequestParam(value="error", required=false)String error) {
		mav.setViewName("login");
		if (error != null) {
			mav.addObject("msg", "ログインできませんでした。");
		} else {
			mav.addObject("msg","ユーザー名とパスワードを入力");
		}
		
		return mav;
	}
	
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
		mav.setViewName("/add");
		return mav;
	}
}
