package com.codestep.TODOapp.Repositories;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codestep.TODOapp.TODOItem;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TODOService {
	
	@Autowired
	TODORepository TODORepository;
	
	public TODOItem create(String username, TODOItem item) {
		item.setUserName(username);
		item.setCreated(LocalDate.now());
		item.setDone(0);
		TODORepository.saveAndFlush(item);
		return item;
	}
}
