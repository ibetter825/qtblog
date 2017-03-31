package com.mypro.web.controller;

import java.io.File;
import java.util.Calendar;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;
import com.mypro.admin.interceptor.AdminContextInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.ResultModel;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolOSSClient;
import com.mypro.web.tools.ToolStrYt;

/**
 * 上传文件
 * @author ibett
 *
 */
@Controller(controllerKey = "/upload")
@Clear({AdminContextInterceptor.class, FrontContextInterceptor.class})
public class UploadController extends BaseController {
	private String rootPath = PathKit.getWebRootPath();
	
	@SuppressWarnings("unchecked")
	public void index(){
		boolean isOss = PropKit.getBoolean(DictKeys.is_upload_to_oss, false);//是否上传到oss
		
		String uploadDefaultDir = PropKit.get(DictKeys.sys_upload_dir_name);
		String uploadAbsolutePath = rootPath;
		String uploadRelativePath = File.separator + uploadDefaultDir;
		String uploadName = getPara("upload_name", "file");
		String uploadType = getPara("upload_type");
		UploadFile uploadFile = null;
		if(uploadType.equalsIgnoreCase("image")){//上传普通图片
			Calendar calendar = Calendar.getInstance();
			uploadRelativePath += File.separator + "image" + File.separator + "other" + File.separator + calendar.get(Calendar.YEAR) + File.separator + (calendar.get(Calendar.MONTH)+1) + File.separator;
		}else
			uploadRelativePath += File.separator + "image" + File.separator + uploadType + File.separator;
		uploadAbsolutePath += uploadRelativePath;
		
		uploadFile = getFile(uploadName);
		String fileName = uploadFile.getOriginalFileName();
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		fileName = System.currentTimeMillis()+"-"+ToolStrYt.getRdmStr(4, false) + fileSuffix;
		ResultModel model = null;
		boolean res = false;
		String path = null;
		if(isOss){
			String ossDomain = PropKit.get(DictKeys.web_oss_domain);//oss地址
			String key = (uploadRelativePath.substring(1) + fileName).replace(File.separator, "/"); //oss key不能以"/"开头
			res = ToolOSSClient.upload(key, uploadFile.getFile());
			if(res) {path = ossDomain+"/"+key; uploadFile.getFile().delete();}
		}else{
			File f = new File(uploadAbsolutePath);
			if(!f.exists()) f.mkdirs();
			res = uploadFile.getFile().renameTo(new File(uploadAbsolutePath, fileName));
			if(res) path = (uploadRelativePath+fileName).replace(File.separator, "/");
		}
		
		if(res){
			model = new ResultModel();
			model.getData().put("path", path);
		}else
			model = new ResultModel("上传失败!");
		renderJson(model);
	}
	public void editor(){
		boolean isOss = PropKit.getBoolean(DictKeys.is_upload_to_oss, false);//是否上传到oss
		
		String uploadDefaultDir = PropKit.get(DictKeys.sys_upload_dir_name);
		String uploadPath = File.separator + uploadDefaultDir;
		String uploadType = getPara("upload_type");
		Calendar calendar = Calendar.getInstance();
		if(uploadType.equalsIgnoreCase("image"))
			uploadPath += File.separator + "image" + File.separator + "other" + File.separator + calendar.get(Calendar.YEAR) + File.separator + (calendar.get(Calendar.MONTH)+1) + File.separator;
		UploadFile uploadFile = getFile("upload");
		String fileName = uploadFile.getOriginalFileName();
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		fileName = System.currentTimeMillis()+"-"+ToolStrYt.getRdmStr(4, false)+fileSuffix;
		
		String uploadAbsolutePath = rootPath + uploadPath;
		
		boolean res = false;
		String path = null;
		if(isOss){
			String ossDomain = PropKit.get(DictKeys.web_oss_domain);//oss地址
			String key = (uploadPath.substring(1) + fileName).replace(File.separator, "/"); //oss key不能以"/"开头
			res = ToolOSSClient.upload(key, uploadFile.getFile());
			if(res) {path = ossDomain+"/"+key; uploadFile.getFile().delete();}
		}else{
			File f = new File(rootPath + uploadPath);
			if(!f.exists()) f.mkdirs();
			res = uploadFile.getFile().renameTo(new File(uploadAbsolutePath, fileName));
			if(res) path = (uploadPath+fileName).replace(File.separator, "/");
		}
		if(res){
			String callback = getPara("CKEditorFuncNum");
			if(callback != null){
				String html = "<script type=\"text/javascript\">";
				html += "window.parent.CKEDITOR.tools.callFunction("+ callback + ",' " + path + "','')"; 
				html += "</script>";
				renderHtml(html);
			}else
				renderText(path);
		}else
			renderText("");
	}
}
