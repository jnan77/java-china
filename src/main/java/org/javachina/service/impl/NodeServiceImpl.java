package org.javachina.service.impl;

import com.blade.ioc.annotation.Service;
import com.blade.jdbc.Paginator;
import org.javachina.dto.NodeDto;
import org.javachina.model.Node;
import org.javachina.service.NodeService;

import java.util.ArrayList;
import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {

	@Override
	public Node getNode(Integer nid) {
		return new Node().where("is_del", 0).where("nid", nid).findOne();
	}

	@Override
	public Node getNode(String slug) {
		return new Node().where("is_del", 0).where("slug", slug).findOne();
	}

	@Override
	public Paginator<Node> getNodes(String orderBy, int page, int limit) {
		if(page < 1){
			page = 1;
		}
		if(limit < 1 || limit > 10){
			limit = 10;
		}
		return new Node().order(orderBy).where("pid <> ?", 0).page(page, limit);
	}

	@Override
	public List<NodeDto> getNodes() {
		List<Node> parents = new Node().where("is_del", 0).where("pid", 0).order("topics desc").list();
		List<NodeDto> list = new ArrayList<NodeDto>();
		for(int i=0,len=parents.size(); i<len; i++){
			Node node = parents.get(i);
			NodeDto nodeDto = new NodeDto(node);
			List<Node> items = new Node().where("is_del", 0).where("pid", node.nid).order("topics desc").list();
			if(null != items){
				nodeDto.items = items;
			}
			list.add(nodeDto);
		}
		return list;
	}

}
