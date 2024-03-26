package com.codestep.TODOapp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codestep.TODOapp.entities.TODOItem;

public interface TODORepository extends JpaRepository<TODOItem, Long>{

}
