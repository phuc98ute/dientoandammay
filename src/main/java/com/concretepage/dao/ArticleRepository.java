package com.concretepage.dao;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.concretepage.entity.*;
public interface ArticleRepository extends CrudRepository<Article, Integer> {
	
	

}
