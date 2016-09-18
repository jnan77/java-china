package org.javachina.service;

import com.blade.jdbc.Paginator;
import org.javachina.dto.NodeDto;
import org.javachina.model.Node;

import java.util.List;

public interface NodeService {
	
	Node getNode(Integer nid);
	
	Node getNode(String slug);

	Paginator<Node> getNodes(String orderBy, int page, int limit);

	List<NodeDto> getNodes();
	
}