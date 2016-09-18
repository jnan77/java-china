package org.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.Paginator;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import org.javachina.model.Favorite;
import org.javachina.model.Topic;
import org.javachina.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteServiceImpl implements FavoriteService {

	@Inject
	private TopicService topicService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private TopicCountService topicCountService;
	
	public Favorite getFavorite(String type, Integer uid, Integer event_id) {
		return new Favorite().where("type", type).where("uid", uid).where("event_id", event_id).findOne();
	}
	
	@Override
	public Integer update(String type, Integer uid, Integer event_id) {
		
		try {
			int count = 0;
			boolean isFavorite = this.isFavorite(type, uid, event_id);
			if(!isFavorite){
				Favorite favorite = new Favorite();
				favorite.type = type;
				favorite.uid = uid;
				favorite.event_id = event_id;
				favorite.create_time = DateKit.getCurrentUnixTime();
				favorite.save();
				count = 1;
			} else {
				Favorite favorite = new Favorite();
				favorite.type = type;
				favorite.uid = uid;
				favorite.event_id = event_id;
				favorite.delete();
				count = -1;
			}

			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean isFavorite(String type, Integer uid, Integer event_id) {
		if (StringKit.isBlank(type) || null == uid || null == event_id) {
			return false;
		}
		return null != this.getFavorite(type, uid, event_id);
	}

	@Override
	public int favorites(String type, Integer uid) {
		if(null != uid && StringKit.isNotBlank(type)){
			return new Favorite().where("type", type).where("uid", uid).count();
		}
		return 0;
	}

	@Override
	public Paginator<Map<String, Object>> getMyTopics(Integer uid, Integer page, Integer count) {
		if(null != uid){
			if(null == page || page < 1){
				page = 1;
			}
			
			if(null == count || count < 1){
				count = 10;
			}

			Paginator<Favorite> faPage = new Favorite().where("type", "topic").where("uid", uid).order("id desc").page(page, count);
			if(null != faPage && faPage.getTotal() > 0){
				long totalCount = faPage.getTotal();
				int page_ = faPage.getPageNum();
				int pageSize = faPage.getLimit();

				Paginator<Map<String, Object>> result = new Paginator<Map<String,Object>>(totalCount, page_, pageSize);
				
				List<Favorite> favorites = faPage.getList();
				
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				if(null != favorites && favorites.size() > 0){
					for(Favorite favorite : favorites){
						Integer tid = favorite.event_id;
						Topic topic = topicService.getTopic(tid);
						/*Map<String, Object> topicMap = topicService.getTopicMap(topic, false);
						if(null != topicMap && !topicMap.isEmpty()){
							list.add(topicMap);
						}*/
					}
				}
				result.setList(list);
				
				return result;
			}
		}
		return null;
	}

	@Override
	public Paginator<Map<String, Object>> getFollowing(Integer uid, Integer page, Integer count) {
		if(null != uid){
			if(null == page || page < 1){
				page = 1;
			}
			if(null == count || count < 1){
				count = 10;
			}

			Paginator<Favorite> faPage = new Favorite().where("type", "following").where("uid", uid).order("id desc").page(page, count);
			if(null != faPage && faPage.getTotal() > 0){
				long totalCount = faPage.getTotal();
				int page_ = faPage.getPageNum();
				int pageSize = faPage.getLimit();
				Paginator<Map<String, Object>> result = new Paginator<Map<String,Object>>(totalCount, page_, pageSize);
				
				List<Favorite> favorites = faPage.getList();
				
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				if(null != favorites && favorites.size() > 0){
					for(Favorite favorite : favorites){
						Integer user_id = favorite.event_id;
//						Map<String, Object> userMap = userService.getUserDetail(user_id);
//						if(null != userMap && !userMap.isEmpty()){
//							list.add(userMap);
//						}
					}
				}
				result.setList(list);
				return result;
			}
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getMyNodes(Integer uid) {
		if(null != uid){
			List<Favorite> favorites = new Favorite().where("type", "node").where("uid", uid).order("id desc").list();
			if(null != favorites && favorites.size() > 0){
				List<Map<String, Object>> result = new ArrayList<Map<String,Object>>(favorites.size());
//				for(Favorite favorite : favorites){
//					Integer nid = favorite.event_id;
//					Map<String, Object> node = nodeService.getNodeDetail(null, nid);
//					result.add(node);
//				}
				return result;
			}
		}
		return null;
	}
	
}
