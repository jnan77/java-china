package org.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.Paginator;
import com.blade.kit.DateKit;
import org.javachina.kit.Utils;
import org.javachina.model.Comment;
import org.javachina.model.Topic;
import org.javachina.model.User;
import org.javachina.service.CommentService;
import org.javachina.service.TopicService;
import org.javachina.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

	@Inject
	private UserService userService;

	@Inject
	private TopicService topicService;

	@Override
	public Comment getComment(Integer cid) {
		return new Comment().findById(cid);
	}

	@Override
	public Paginator<Map<String, Object>> getPageListMap(Integer tid, Integer uid, String orderby, int page, int count) {

		Paginator<Comment> pager = new Comment().where("tid", tid).where("uid", uid).order(orderby).page(page, count);
		if (null != pager) {
			return this.getCommentPageMap(pager);
		}
		return null;
	}

	private Paginator<Map<String, Object>> getCommentPageMap(Paginator<Comment> commentPage) {

		long totalCount = commentPage.getTotal();
		int page = commentPage.getPageNum();
		int limit = commentPage.getLimit();

		Paginator<Map<String, Object>> result = new Paginator<Map<String, Object>>(totalCount, page, limit);

		List<Comment> comments = commentPage.getList();
		List<Map<String, Object>> nodeMaps = new ArrayList<Map<String, Object>>();
		if (null != comments && comments.size() > 0) {
			for (Comment comment : comments) {
				Map<String, Object> map = this.getCommentDetail(comment, null);
				if (null != map && !map.isEmpty()) {
					nodeMaps.add(map);
				}
			}
		}
		result.setList(nodeMaps);
		return result;
	}

	private Map<String, Object> getCommentDetail(Comment comment, Integer cid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (null == comment) {
			comment = this.getComment(cid);
		}
		if (null != comment) {

			Integer comment_uid = comment.uid;
			User comment_user = userService.getUser(comment_uid);
			Topic topic = topicService.getTopic(comment.tid);
			if (null == comment_user || null == topic) {
				return map;
			}

			map.put("cid", comment.cid);
			map.put("tid", comment.tid);
			map.put("role_id", comment_user.role_id);
			map.put("reply_name", comment_user.login_name);
			map.put("reply_time", comment.create_time);
			map.put("device", comment.device);
			map.put("reply_avatar", comment_user.avatar);
			map.put("title", topic.title);
			String content = Utils.markdown2html(comment.content);
			map.put("content", content);
		}
		return map;
	}

	@Override
	public Integer save(Integer uid, Integer toUid, Integer tid, String content, String ua) {
		Comment comment = new Comment();
		comment.uid = uid;
		comment.to_uid = toUid;
		comment.tid = tid;
		comment.content = content;
		comment.device = ua;
		comment.create_time = DateKit.getCurrentUnixTime();

		try {
			return comment.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean delete(Integer cid) {
		if (null != cid) {
			Comment comment = new Comment();
			comment.cid = cid;
			comment.delete();
			return true;
		}
		return false;
	}

	@Override
	public Comment getTopicLastComment(Integer tid) {
		return new Comment().where("tid", tid).order("cid desc").findOne();
	}

	@Override
	public int getComments(Integer uid) {
		return new Comment().where("uid", uid).count();
	}

}
