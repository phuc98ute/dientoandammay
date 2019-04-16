package com.concretepage.service;

import java.util.List;

import org.springframework.security.access.annotation.Secured;

import com.concretepage.entity.Article;

public interface IUserInfoService {
	 @Secured ({"ROLE_ADMIN"})
     List<Article> getAllUserArticles();
	 Article FindOne(int id);
	 Iterable<Article> FindAll();
	
	 void Save(Article ar);
	 void Delete(Article ar);
	 
}
