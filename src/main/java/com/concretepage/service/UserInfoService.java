package com.concretepage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concretepage.dao.IUserInfoDAO;
import com.concretepage.dao.ArticleRepository;
import com.concretepage.entity.Article;
@Service
public class UserInfoService implements IUserInfoService {
	@Autowired
	private IUserInfoDAO userInfoDAO;
	@Autowired
    private ArticleRepository ArticleRepository;

	public List<Article> getAllUserArticles(){
		return userInfoDAO.getAllUserArticles();
	}
	public Article FindOne(int id) {
		
		return ArticleRepository.findOne(id);
	}
	public Iterable<Article> FindAll() {
		// TODO Auto-generated method stub
		return ArticleRepository.findAll();
	}
	
	public void Save(Article ar) {
		ArticleRepository.save(ar);
		
	}
	public void Delete(Article ar) {
		ArticleRepository.delete(ar);
	
		
	}
}
