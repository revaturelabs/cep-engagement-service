package com.cepengagementservice.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cepengagementservice.Repositories.ProfileCategoryRepository;

@Service
public class ProfileCategoryService {

	private ProfileCategoryRepository pcRepo;
	
	private ProfileService profileServ;
	
	private CategoryService categoryService;
	
	public ProfileCategoryService() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public ProfileCategoryService(ProfileCategoryRepository pcRepo, ProfileService profileServ,
			CategoryService categoryService) {
		super();
		this.pcRepo = pcRepo;
		this.profileServ = profileServ;
		this.categoryService = categoryService;
	}
	
	
}
