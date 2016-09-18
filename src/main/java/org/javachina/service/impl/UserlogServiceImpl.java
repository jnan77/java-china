package org.javachina.service.impl;

import com.blade.context.WebApplicationContext;
import com.blade.ioc.annotation.Service;
import com.blade.kit.DateKit;
import org.javachina.kit.Utils;
import org.javachina.model.Userlog;
import org.javachina.service.UserlogService;


@Service
public class UserlogServiceImpl implements UserlogService {
	
	@Override
	public void save(final Integer uid, final String action, final String content) {
		final String ip = Utils.getIpAddr(WebApplicationContext.request());
		Runnable t = new Runnable() {
			@Override
			public void run() {
				try {
					Userlog userlog = new Userlog();
					userlog.uid = uid;
					userlog.action = action;
					userlog.content = content;
					userlog.ip_addr = ip;
					userlog.create_time = DateKit.getCurrentUnixTime();
					userlog.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Utils.run(t);
	}
	
}