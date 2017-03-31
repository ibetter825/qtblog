package com.mypro.web.controller.front;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.front.validator.ArticleValidator;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysDict;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtArticleCount;
import com.mypro.model.front.QtArticleType;
import com.mypro.model.front.QtTag;
import com.mypro.service.admin.SysDictService;
import com.mypro.service.front.QtArticleService;
import com.mypro.service.front.QtArticleTypeService;
import com.mypro.service.front.QtCountService;
import com.mypro.service.front.QtTagService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolStrYt;
/**
 * 个人中心
 * @author ibett
 *
 */
@Controller(controllerKey = "/article")
//@Clear
public class ArticleController extends BaseController {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void write(){
		//查询文章栏目
		List<Map> dicts = SysDictService.service.queryDictList(new QueryModel().set("dict_param", "article"));
		setAttr("dicts", SysDict.getFrontLis(dicts, null));
		//查询常用标签和热门标签
		Integer acc_no = getCurrentAccount().getInt("acc_no");
		Map<String, Object> tags = QtTagService.service.queryWriteRmdTags(acc_no);
		Map<String, Integer> mapping = (Map<String, Integer>) tags.get("mapping");
		if(mapping != null)
			setAttr("mapping", JsonKit.toJson(mapping));
		//查询用户自定义的文章分类
		List<QtArticleType> types = QtArticleTypeService.service.queryAtArticleTypes(new QueryModel().set("acc_no", acc_no));
		if(types != null)
			setAttr("types", QtArticleType.getFrontOptions(types, -1));
		setAttr("tags", QtTag.getWriteRmdTags(tags));
		setAttr("redirctUrl", getRefererURI());
		renderWithHeader(basePath+"/write.html");
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void edit(){
		Integer article_no = getParaToInt(0);
		//判断当前登录的用户是否是该文章的作者
		QtAccount account = getCurrentAccount();//判断当前登录用户是否为文章作者,只有作者才能删除该文章
		Integer acc_no = null;
		boolean flag = false;
		QtArticle article = null;
		if(account != null){
			article = QtArticleService.service.queryQtArticleByNo(article_no, null);
			if(article != null){
				Integer art_acc = article.getInt("acc_no");
				acc_no = account.getInt("acc_no");
				if(art_acc.equals(acc_no)){
					flag = true;
					setAttr("dto", article);
				}
			}
		}
		if(flag){
			//查询文章分类
			List<Map> dicts = SysDictService.service.queryDictList(new QueryModel().set("dict_param", "article"));
			setAttr("dicts", SysDict.getFrontLis(dicts, article.getStr("article_theme")));
			//查询常用标签和热门标签
			Map<String, Object> tags = QtTagService.service.queryWriteRmdTags(getCurrentAccount().getInt("acc_no"));
			Map<String, Integer> mapping = (Map<String, Integer>) tags.get("mapping");
			//将文章存在的标签也一起放进mapping
			String exist_names = article.get("article_tags", null);
			if(StringUtils.isNotEmpty(exist_names)){
				String exist_ids = article.get("article_tag_ids", null);
				String[] exist_name_arr = exist_names.split(",");
				String[] exist_id_arr = exist_ids.split(",");
				for(int i = 0, l = exist_name_arr.length; i < l; i++){
					if(mapping == null) mapping = new HashMap<String, Integer>();
					mapping.put(exist_name_arr[i], Integer.valueOf(exist_id_arr[i]));
				}
			}
			if(mapping != null)
				setAttr("mapping", JsonKit.toJson(mapping));
			//查询用户自定义的文章分类
			List<QtArticleType> types = QtArticleTypeService.service.queryAtArticleTypes(new QueryModel().set("acc_no", acc_no));
			if(types != null)
				setAttr("types", QtArticleType.getFrontOptions(types, (int) article.get("article_type", 0)));
			setAttr("tags", QtTag.getWriteRmdTags(tags));
		}
		setAttr("redirctUrl", getRefererURI());
		renderWithHeader(basePath+"/write.html");
	}
	/**
	 * 修改文章状态
	 */
	public void state(){
		Integer article_no = getParaToInt(0);
		//判断当前登录的用户是否是该文章的作者
		QtAccount account = getCurrentAccount();//判断当前登录用户是否为文章作者,只有作者才能删除该文章
		Integer acc_no = null;
		QtArticle article = null;
		boolean res = false;
		if(account != null){
			article = QtArticleService.service.queryQtArticleByNo(article_no, -1);
			if(article != null){
				Integer art_acc = article.getInt("acc_no");
				acc_no = account.getInt("acc_no");
				if(art_acc.equals(acc_no)){
					Integer state = getParaToInt(1);
					if(state != null){
						res = QtArticleService.service.editQtArticle(new QtArticle()
																		.set("article_no", article_no)
																		.set("article_state", state)
																		.set("update_time", ToolDateTime.getDateByTime()), null);
					}
				}
			}
		}
		ResultModel model = null;
		if(res)
			model = new ResultModel();
		else
			model = new ResultModel("操作失败");
		renderJson(model);
	}
	public void del(){
		Integer article_no = getParaToInt(0);
		//判断当前登录的用户是否是该文章的作者
		QtAccount account = getCurrentAccount();//判断当前登录用户是否为文章作者,只有作者才能删除该文章
		boolean flag = false;
		if(account != null){
			QtArticle article = QtArticleService.service.queryQtArticleByNo(article_no, null);
			Integer art_acc = article.getInt("acc_no");
			Integer acc_no = account.getInt("acc_no");
			if(art_acc.equals(acc_no))
				flag = true;
		}
		if(flag)
			flag = QtArticleService.service.editQtArticle(new QtArticle().set("article_no", article_no).set("article_state", -1), null);
		ResultModel model = null;
		if(flag)
			model = new ResultModel();
		else
			model = new ResultModel("删除失败");
		renderJson(model);
	}
	@Before(ArticleValidator.class)
	public void save(){
		QtArticleService service = Duang.duang(QtArticleService.class);
		QtArticle article = getModel(QtArticle.class, "art");
		//判断 敏感词 以及后台验证表单内容
		//将title与summary转义
		article.set("article_title", ToolStrYt.escapeHtml(article.getStr("article_title")));
		article.set("article_summary", ToolStrYt.escapeHtml(article.getStr("article_summary")).replaceAll("[\\s]", ""));
		Integer art_no = article.get("article_no");
		boolean result = false;
		QtAccount account = getCurrentAccount();
		Integer acc_no = account.getInt("acc_no");
		article.set("acc_no", acc_no);
		if(art_no == null){
			long time = ToolDateTime.getDateByTime();
			article.set("add_time", time);
			article.set("update_time", time);
			result = service.saveQtArticle(article);
		}else
			result = service.editQtArticle(article, getPara("article_old_tag_ids", null));
		if(result){
			final Integer no = article.get("article_no");
			final String domain = getRequestDomain();
			//添加和修改后都静态化文章
			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
					//静态化文章
					com.mypro.service.admin.QtArticleService.service.staticArticle(no, domain);
				}
			});
			thread.start();
			renderJson(new ResultModel());
		}else
			renderJson(new ResultModel("发表失败"));
	}
	public void list(){
		Integer cur_page = getParaToInt(0, 1);
		Integer page_size = getParaToInt(1, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		QtAccount account = getCurrentAccount();
		String acc_no = account.getStr("acc_no");
		QueryModel rq = new QueryModel();
		rq.set("a.article_state[>]", -1);
		rq.set("a.acc_no", acc_no);
		Page<QtArticle> page = QtArticleService.service.queryQtArtByParams(pager, rq);
		List<QtArticle> articles = page.getList();
		renderHtml(QtArticle.getArtcilesHtml(articles, false));
	}
	/**
	 * 跳转文章详情页面
	 */
	@Clear(FrontContextInterceptor.class)
	public void detail(){
		Integer article_no = getParaToInt(0);
		QtArticle article = QtArticleService.service.queryQtArticleByNo(article_no, null);
		//需要将文章的上下两篇文章查询出来
		if(article != null){
			//私密文章只能作者自己查看
			int flg = article.getInt("article_flag");
			if(flg == 1){
				QtAccount account = getCurrentAccount();
				if(account != null){
					String author = article.getStr("acc_no");
					String accno = account.getStr("acc_no");
					if(!author.equals(accno)){
						renderError(404);
						return;
					}
				}else{
					renderError(404);
					return;
				}
			}
			List<QtArticle> articles = QtArticleService.service.queryQtArticleNextAndPrev(article);
			for (QtArticle art : articles) {
				if(art.getLong("add_time") > article.getLong("add_time"))
					setAttr("next", art);
				else
					setAttr("prev", art);
			}
			setAttr("dto", article);
			setAttr("time", ToolDateTime.getDate(article.getLong("add_time"), ToolDateTime.pattern_ymd_hms));
			String str = article.getStr("article_tags");//标签
			if(StringUtils.isNotEmpty(str)){
				StringBuffer buffer = new StringBuffer("<div class=\"article-tags\"> 标签：");
				String[] arr = str.split(",");
				String[] ids = article.getStr("article_tag_ids").split(",");
				for(int i = 0, l = arr.length; i < l; i++)
					buffer.append("<a href=\""+getCtx()+"/tag/"+ids[i]+"\" rel=\"tag\">"+arr[i]+"</a>");
				buffer.append("</div>");
				setAttr("tags", buffer.toString());
			}
			String img = article.getStr("article_imgs");
			if(StringUtils.isNotEmpty(img))
				setAttr("img", img.split(";")[0]);
			renderWithHeader(basePath+"/detail.html", article.getStr("article_theme"));
		}else
			renderError(404);
	}
	/**
	 * 获取浏览数量,同时增加浏览量,作者昵称，发布时间，栏目
	 */
	@Clear(FrontContextInterceptor.class)
	public void info(){
		Integer article_no = getParaToInt("artno");
		Integer art_acc = getParaToInt("author");
		//int count = QtArticleService.service.queryQtArtScanCount(article_no);
		QtAccount account = getCurrentAccount();//判断当前登录用户是否为文章作者
		boolean isauth = false;
		if(account != null){
			Integer acc_no = account.getInt("acc_no");
			if(art_acc.equals(acc_no))
				isauth = true;
		}
		QtArticle article = QtArticleService.service.queryArticleInfo(article_no);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<li><a id=\"art-acc-name\" href=\"/main/"+article.getInt("acc_no")+"\">"+article.getStr("nick_name")+"</a> 发布于 <span>"+ToolDateTime.getDate(article.getLong("add_time"), "yyyy-MM-dd")+"</span></li>");
		buffer.append("<li>栏目：<a href=\"/"+article.getStr("article_theme")+"\" rel=\"category tag\" title=\""+article.getStr("dict_name")+"\">"+article.getStr("dict_name")+"</a></li>");
		buffer.append("<li><span class=\"post-views\" title=\"浏览\"><i class=\"glyphicon glyphicon-eye-open\"></i> 浏览("+ (article.getInt("scan_count")+1) +")</span></li>");
		if(isauth){
			buffer.append("<li><a href=\"/article/edit/"+article_no+"\" title=\"编辑\"><i class=\"glyphicon glyphicon-edit\"></i> 编辑</a></li>");
			buffer.append("<li><a href=\"javascript:void(0);\" onclick=\"art.remove('"+article_no+"')\" title=\"删除\"><i class=\"glyphicon glyphicon-trash\"></i> 删除</a></li>");//删除需要弹出确认框才行，然后传到前台页面的编号这些最好需要加一下密
		}
		buffer.append("<li><a href=\"javascript:void(0);\" onclick=\"return art.report(3,'"+article_no+"',this);\" title=\"举报\"><i class=\"glyphicon glyphicon-exclamation-sign\"></i> 举报</a></li>");
		renderHtml(buffer.toString());
	}
	/**
	 * 点赞，转载，转载等操作
	 */
	@Clear(FrontContextInterceptor.class)
	public void opts(){
		String art_no = getPara("artno");
		QtAccount account = getCurrentAccount();//当前登录用户
		String res = "";
		if(StringUtils.isNotEmpty(art_no)){
			QtArticleCount count = null;
			if(account != null)
				count = QtCountService.service.queryByPK(art_no, account.getInt("acc_no"));
			else
				count = QtCountService.service.queryByPK(art_no, null);
			res = QtArticle.getArticleOptsHtml(count);
		}
		renderJsonp("'"+res+"'");
	}
	/**
	 * 根据count查询
	 */
	@Clear(FrontContextInterceptor.class)
	public void bycount(){
		PagerModel pager = new PagerModel(this);
		QueryModel rq = new QueryModel(this, "a", "c");
		rq.set("a.article_state", 1);
		Page<QtArticle> page = QtArticleService.service.queryQtArtByCount(pager, rq);
		List<QtArticle> articles = page.getList();
		renderJsonp(JsonKit.toJson(articles));
	}
	/**
	 * 根据评论排序查询文章
	 */
	public void bycomt(){
		PagerModel pager = new PagerModel(this);
		pager.setSortName("comt_count");
		pager.setSortOrder("desc");
		QueryModel rq = new QueryModel();
		String acc_no = getCurrentAccount().getStr("acc_no");
		rq.set("a.acc_no", acc_no);
		Page<QtArticle> page = QtArticleService.service.queryQtArticleByComtCount(pager, rq);
		List<QtArticle> articles = page.getList();
		renderJsonp(JsonKit.toJson(articles));
	}
	/**
	 * 查询用户的文章,一共有0:用户编号,1:文章类型,2:当前页码,3:每一页的数量
	 * 当用户编号为"article"时表示查询当前登录用户的自己的文章,个人中心查询文章，不是"article"为查询个人主页的文章，这里还有问题，如果修改一下参数"article"的话，编辑与删除图标就会出来，需要修改
	 */
	@Clear(FrontContextInterceptor.class)
	public void arts(){
		String acc_no = getPara(0);
		QueryModel rq = new QueryModel();
		boolean self = false;
		if(acc_no.equalsIgnoreCase("article")){
			rq.set("a.article_state[>]", -1);
			QtAccount account = getCurrentAccount();
			if(account != null){
				acc_no = account.getStr("acc_no");
				self = true;
			}else
				renderHtml("");
		}else
			rq.set("a.article_state", 1);
		int type = getParaToInt(1, -1);
		int cur_page = getParaToInt(2, 1);
		int page_size = getParaToInt(3, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		pager.setSortName("a.add_time");
		pager.setSortOrder("desc");
		//如果是-1则是查询该用户的全部文章
		
		rq.set("a.acc_no", acc_no);
		if(type != -1)
			rq.set("a.article_type[=]", type);

		Page<QtArticle> page = QtArticleService.service.queryQtArtByParams(pager, rq);
		List<QtArticle> articles = page.getList();
		renderHtml(QtArticle.getArtcilesHtml(articles, self));
	}
	/**
	 * 文章的相关推荐
	 */
	@Clear(FrontContextInterceptor.class)
	public void similar(){
		//只查询同栏目下的随机的四条数据
	}
}
