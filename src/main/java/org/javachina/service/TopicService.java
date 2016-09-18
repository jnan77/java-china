package org.javachina.service;

import com.blade.jdbc.Paginator;
import org.javachina.dto.TopicDto;
import org.javachina.model.Topic;

import java.util.List;

public interface TopicService {
	
	Topic getTopic(Integer tid);
	
	TopicDto getTopicDetail(Integer tid);

	long getTopics(String user_name);

	Paginator<TopicDto> getTopics(Integer nid, int page, int limit);

	List<TopicDto> getTodayTopics(int limit);
	
}
