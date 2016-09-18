package org.javachina.service;

import com.blade.jdbc.Paginator;
import org.javachina.model.Comment;

import java.util.Map;

public interface CommentService {
	
	Comment getComment(Integer cid);
		
	Comment getTopicLastComment(Integer tid);

	Paginator<Map<String, Object>> getPageListMap(Integer tid, Integer uid, String orderby, int page, int count);
	
	Integer save( Integer uid, Integer toUid, Integer tid, String content, String ua);
	
	boolean delete(Integer cid);
	
	int getComments(Integer uid);
		
}
