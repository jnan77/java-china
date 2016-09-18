package org.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.Paginator;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import org.javachina.model.Comment;
import org.javachina.model.Notice;
import org.javachina.model.Topic;
import org.javachina.model.User;
import org.javachina.service.CommentService;
import org.javachina.service.NoticeService;
import org.javachina.service.TopicService;
import org.javachina.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private CommentService commentService;
	
	@Override
	public boolean save(String type, Integer to_uid, Integer event_id) {
		if(StringKit.isNotBlank(type) && null != to_uid && null != event_id){
			Notice notice = new Notice();
			notice.type = type;
			notice.to_uid = to_uid;
			notice.event_id = event_id;
			notice.create_time = DateKit.getCurrentUnixTime();
			notice.save();
			return true;
		}
		return false;
	}
	
	@Override
	public Paginator<Map<String, Object>> getNoticePage(Integer uid, Integer page, Integer count) {
		if(null != uid){
			if(page < 1) page = 1;
			if(count < 1) count = 10;
			Paginator<Notice> pager = new Notice().where("to_uid", uid).order("id desc").page(page, count);
			return this.getNoticePageMap(pager);
		}
		return null;
	}
	
	private Paginator<Map<String, Object>> getNoticePageMap(Paginator<Notice> noticePage){
		long totalCount = noticePage.getTotal();
		int page = noticePage.getPageNum();
		int pageSize = noticePage.getLimit();
		Paginator<Map<String, Object>> pageResult = new Paginator<Map<String,Object>>(totalCount, page, pageSize);
		
		List<Notice> notices = noticePage.getList();
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		if(null != notices){
			for(Notice notice : notices){
				Map<String, Object> map = this.getNotice(notice);
				if(null != map && !map.isEmpty()){
					result.add(map);
				}
			}
		}
		pageResult.setList(result);
		
		return pageResult;
	}
	
	private Map<String, Object> getNotice(Notice notice){
		if(null == notice){
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Integer uid = notice.to_uid;
		User user = userService.getUser(uid);
		if(null == user){
			return null;
		}
		map.put("id", notice.id);
		map.put("type", notice.type);
		map.put("create_time", notice.create_time);
		map.put("user_name", user.login_name);
		
		if(notice.type.equals("comment_at") || notice.type.equals("comment")){
			Comment comment = commentService.getComment(notice.event_id);
			if(null != comment){
				Topic topic = topicService.getTopic(comment.tid);
				if(null != topic){
					String title = topic.title;
					map.put("title", title);
					//map.put("content", content);
					map.put("tid", topic.tid);
				}
			}
		}
		
		if(notice.type.equals("topic_at")){
			Topic topic = topicService.getTopic(notice.event_id);
			if(null != topic){
				String title = topic.title;
//				String content = Utils.markdown2html(topic.content);
				
				map.put("title", title);
//				map.put("content", content);
				map.put("tid", topic.tid);
			}
		}
		
		return map;
	}

	@Override
	public boolean read(Integer to_uid) {
		if(null != to_uid){
			// 删除
			try {
				Notice notice = new Notice();
				notice.is_read = true;
				notice.to_uid = to_uid;
				notice.update();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public int getNotices(Integer uid) {
		if(null != uid){
			return new Notice().where("is_read", 0).where("to_uid", uid).count();
		}
		return 0;
	}

}
