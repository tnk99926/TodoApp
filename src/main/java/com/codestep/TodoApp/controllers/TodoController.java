package com.codestep.TodoApp.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codestep.TodoApp.entities.TodoItem;
import com.codestep.TodoApp.services.TodoService;

@Controller
public class TodoController {
	
	@Autowired
	TodoService todoService;
	
	@Autowired
	UserDetailsManager userDetailsManager;
	
	
	@GetMapping("/add")
	public ModelAndView showAddForm(ModelAndView mav, @ModelAttribute TodoItem item) {
		mav.addObject("item", item);
		mav.setViewName("/add");
		return mav;
	}
	
	@PostMapping("/add")
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
	
	record TodoItemEx(TodoItem item, String strDone,String appendClass) {
	};
	enum dueStatus{
		NORMAL("normal"),
		CAUTION("caution"),
		OVERDUE("overdue");
		
		private String htmlClassName;
		dueStatus(String htmlClassName){
			this.htmlClassName = htmlClassName;
		}
	}
	
	@GetMapping("/list")
	public ModelAndView showList(ModelAndView mav, @ModelAttribute("errmsg") String errMsg) {
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
			String appendClass;
			
			TodoItemEx todoItemEx;
			if (deadline == null) {
				appendClass = dueStatus.NORMAL.htmlClassName;
			} else if(todoItem.isDone()) {
				appendClass = dueStatus.NORMAL.htmlClassName;//完了済み
			} else if(deadline.isBefore(now)){
				appendClass = dueStatus.OVERDUE.htmlClassName;//期日超過
			} else if(deadline.isBefore(notifyDt)){
				appendClass = dueStatus.CAUTION.htmlClassName;//期日7日以内
			} else {
				appendClass = dueStatus.NORMAL.htmlClassName;//それ以外
			}
			todoItemEx = new TodoItemEx(todoItem, strDone, appendClass);
			listEx.add(todoItemEx);
		}
		
		mav.addObject("loginUserName",username);
		mav.addObject("role",user.getAuthorities());
		mav.addObject("listEx",listEx);
		mav.addObject("errmsg", errMsg);
		mav.setViewName("list");
		return mav;
	}
	
	@GetMapping("/item/{id}")
	public ModelAndView showItem(ModelAndView mav, @PathVariable int id, RedirectAttributes redirectAttributes) {
		try {
			if(!todoService.isLoginUserOrAdmin(id)) {
				mav.setViewName("redirect:/list");
				return mav;
			}
			TodoItem todoItem = todoService.getById(id);
			
			LocalDate now = LocalDate.now();
			
			LocalDate notifyDt = now.plusDays(7);
			
			String strDone = todoService.getStrDone(todoItem);
			
			LocalDate deadline = todoItem.getDeadline();
			String appendClass;
			
			TodoItemEx todoItemEx;
			if (deadline == null) {
				appendClass = dueStatus.NORMAL.htmlClassName;
			} else if(todoItem.isDone()) {
				appendClass = dueStatus.NORMAL.htmlClassName;//完了済み
			} else if(deadline.isBefore(now)){
				appendClass = dueStatus.OVERDUE.htmlClassName;//期日超過
			} else if(deadline.isBefore(notifyDt)){
				appendClass = dueStatus.CAUTION.htmlClassName;//期日7日以内
			} else {
				appendClass = dueStatus.NORMAL.htmlClassName;//それ以外
			}
			todoItemEx = new TodoItemEx(todoItem, strDone, appendClass);
			
			mav.addObject("itemEx",todoItemEx);
			mav.setViewName("item");
			return mav;
		} catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errmsg", "TODO ID=" + id + "の詳細表示に失敗しました。");
			mav.setViewName("redirect:/list");
			return mav;
		}
	}
	
	@PostMapping("/complete")
	public ModelAndView completeItem(ModelAndView mav, @RequestParam long id,@RequestParam("progress") String progress, RedirectAttributes redirectAttributes) {
		
	    try {
	    	if(!todoService.isLoginUserOrAdmin(id)) {
				mav.setViewName("redirect:/list");
				return mav;
			}
			if(progress.equals("in_progress")) {
				todoService.complete(id, true);
			} else {
				todoService.complete(id, false);	
			}
		    mav.setViewName("redirect:/item/" + id);
		    return mav;
		    
	    } catch (Exception e){
	   		e.printStackTrace();
	   		redirectAttributes.addFlashAttribute("errmsg", "TODO ID=" + id + "の進捗状態の変更に失敗しました。");
	   		mav.setViewName("redirect:/list");
			return mav;
	    }
	  }
	
	@PostMapping("/delete")
	public ModelAndView deleteItem(ModelAndView mav, @RequestParam long id, RedirectAttributes redirectAttributes) {
		try {
			if(todoService.isLoginUserOrAdmin(id)) {
				todoService.delete(id);
			}
		} catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errmsg", "TODO ID=" + id + "の削除に失敗しました。");
		}
		mav.setViewName("redirect:/list");
		return mav;
	}
	
	@GetMapping("/update/{id}")
	public ModelAndView showUpdateForm(ModelAndView mav, @PathVariable long id, RedirectAttributes redirectAttributes) {
		try {
			if(!todoService.isLoginUserOrAdmin(id)) {
				mav.setViewName("redirect:/list");
				return mav;
			}
			TodoItem todoItem = todoService.getById(id);
			mav.addObject("item", todoItem);
			mav.setViewName("/update");
			return mav;
		} catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errmsg", "TODO ID=" + id + "の更新画面の表示に失敗しました。");
			mav.setViewName("redirect:/list");
			return mav;
		}
	}
	
	@PostMapping("/update")
	public ModelAndView postUpdateForm(ModelAndView mav, @ModelAttribute("item") @Validated TodoItem item, BindingResult result, @RequestParam("done-reset") String doneReset, RedirectAttributes redirectAttributes ) {
		try {
			if(!todoService.isLoginUserOrAdmin(item.getId())) {
				mav.setViewName("redirect:/list");
				return mav;
			} 
			if(result.hasErrors()) {
				mav.addObject("item", item);
				mav.setViewName("/update");
				return mav;
			} 
			todoService.update(item.getId(), item.getTitle(), item.getDeadline(), doneReset);
			mav.setViewName("redirect:/item/" + item.getId());
			return mav;
		} catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errmsg", "TODO ID=" + item.getId()+ "の更新に失敗しました。");
			mav.setViewName("redirect:/list");
			return mav;
		}
		
	}
}
