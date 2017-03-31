package com.mypro.service.front;

import java.util.List;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.mypro.model.front.QtLink;

public class QtLinkService {
	public final static QtLinkService service = new QtLinkService();

	public List<QtLink> queryLinks(){
		List<QtLink> links = CacheKit.get("commonCache", "links", new IDataLoader(){
			public Object load(){
				return QtLink.dao.find("select " + QtLink.all + " from qt_link order by link_seq");
			}
		});
		return links;
	}
}
