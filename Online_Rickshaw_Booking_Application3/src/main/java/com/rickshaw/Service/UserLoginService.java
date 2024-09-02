package com.rickshaw.Service;

import com.rickshaw.Exception.AdminException;
import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Exception.CustomerException;
import com.rickshaw.Model.CurrentUserSession;
import com.rickshaw.Model.UserLoginDTO;

public interface UserLoginService {

public CurrentUserSession login(UserLoginDTO dto)throws CustomerException, AdminException;
	
	public String LogOut(String uuid)throws CurrentUserSessionException; 
	
	
}
