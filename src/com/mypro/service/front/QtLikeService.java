package com.mypro.service.front;

import com.mypro.model.front.QtArticleLike;

/**
 * 点赞
 * @author ibett
 *
 */
public class QtLikeService {
	public final static QtLikeService service = new QtLikeService();
	public boolean saveQtArtLike(QtArticleLike like){
		return like.save();
	}
	public boolean removeQtArtLike(QtArticleLike like){
		return like.delete();
	}
}
