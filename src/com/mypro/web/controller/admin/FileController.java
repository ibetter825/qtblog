package com.mypro.web.controller.admin;

import java.io.File;
import org.apache.commons.lang.StringUtils;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.model.ResultModel;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolOSSClient;

/**
 * 上传文件
 * @author ibett
 *
 */
@Controller(controllerKey = "/admin/file")
public class FileController extends BaseController {
	private String rootPath = PathKit.getWebRootPath();
	public void del(){
		boolean isOss = PropKit.getBoolean(DictKeys.is_upload_to_oss, false);//是否上传到oss
		String relativePath = getPara("path");
		boolean res = false;
		if(StringUtils.isNotEmpty(relativePath)){
			if(isOss){
				String key = relativePath.substring(relativePath.indexOf(".com/") + 5);
				res = ToolOSSClient.delete(key);
			}else{
				String absolutePatth = rootPath + relativePath;
				File file = new File(absolutePatth);
				if(file.exists())
					res = file.delete();
			}
		}
		
		ResultModel model = null;
		if(res){
			model = new ResultModel();
		}else
			model = new ResultModel("删除失败!");
		renderJson(model);
	}
}
