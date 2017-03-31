/**
 * 
 */
package com.mypro.common.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author jack.li
 * 
 */
public class HttpClientUtils {

	public static String get(String url) {
		// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
				// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
		HttpURLConnection connection = null;
		// 取得输入流，并使用Reader读取
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			URL getUrl = new URL(url);
			connection = (HttpURLConnection) getUrl
					.openConnection();
			// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
			// 服务器
			connection.connect();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(connection != null)
				connection.disconnect();
		}
		return sb.toString();
	}

	public static String post(String url) {
		// Post请求的url，与get不同的是不需要带参数
		// 打开连接
		HttpURLConnection connection = null;
		// 取得输入流，并使用Reader读取
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			URL postUrl = new URL(url);
			connection = (HttpURLConnection) postUrl
					.openConnection();
			// 设置是否向connection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// 有的请求不支持POST请求
			// connection.setRequestMethod( "POST" );
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			// connection.setRequestProperty("Content-Type"
			// ,"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Type", "text/html");
			connection.connect();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(connection != null)
			connection.disconnect();
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// readContentFromGet();
		String str = HttpClientUtils.get("http://www.baidu.com");
		System.out.println(str);
	}
}
