package com.mypro.admin.interceptor;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.mypro.model.ResultModel;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolStrYt;
/**
 * 错误拦截器
 * @author ibett
 *
 */
public class ExceptionInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		BaseController controller = (BaseController) inv.getController();
		HttpServletRequest request = controller.getRequest();
		try {
			inv.invoke();
		} catch (Exception e) {
			e.printStackTrace();
			String header = request.getHeader("X-Requested-With");  
            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(header);  
            //String msg = formatException(e);  
            if(isAjax){  
            	header = request.getHeader("X-PJAX");//判断是否是pjax请求
        		if(Boolean.valueOf(header))
        			controller.renderHtml("PJAX ERROR");
        		else{
        			ResultModel model = new ResultModel("-1", "处理信息出错，请稍后再试");
        			controller.renderJson(model);
        		}
            }else{  
                String redirctUrl = request.getHeader("referer");  
                if(StringUtils.isBlank(redirctUrl)){  
                    redirctUrl = request.getRequestURI();  
                }  
                //controller.redirect("/common/error?message="+ToolStrYt.UrlEncode(ToolStrYt.UrlEncode(formatException(e)))+"&redirectUrl="+ToolStrYt.UrlEncode(redirctUrl));
                controller.redirect("/common/error?redirectUrl="+ToolStrYt.UrlEncode(redirctUrl));
            }  
		}
	}
	/**
	   * 格式化异常信息，用于友好响应用户
	   * @param e
	   * @return
	   */
	@SuppressWarnings("unused")
	private static String formatException(Exception e){
		String message = null;
	    Throwable ourCause = e;
	    while ((ourCause = e.getCause()) != null) {
	       e = (Exception) ourCause;
	    }
	
	    if (e instanceof RuntimeException) {
	        message = e.getMessage();
	        if(StringUtils.isBlank(message))message = e.toString();
	    }
	      
	    //获取默认异常提示
	    if (StringUtils.isBlank(message)){
	       message = "系统繁忙,请稍后再试";
	    }
	    //替换特殊字符
	    message = message.replaceAll("\"", "'");
	    return message;
	  }

}
