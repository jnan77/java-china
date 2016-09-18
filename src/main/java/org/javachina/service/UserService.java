package org.javachina.service;

import com.blade.jdbc.Paginator;
import org.javachina.model.LoginUser;
import org.javachina.model.User;

public interface UserService {
	
	User getUser(Integer uid);

	Paginator<User> getPageList(Integer status, Integer uid, String orderby, int page, int count);
	
	User getUser(String username, String password);

	LoginUser getLoginUser(User user);
}
