package com.mypro.web.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolCrawler {
	public static void main(String[] args) {
		String sUrl = "http://www.taomingyan.com/article/27109.html";
		URL url;  
        try {  
            url = new URL(sUrl);  
            URLConnection urlconnection = url.openConnection();  
            urlconnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");  
            InputStream is = url.openStream();  
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));  
            StringBuffer sb = new StringBuffer();//sb为爬到的网页内容  
            String rLine = null;  
            while((rLine=bReader.readLine())!=null)
                sb.append(rLine);
            //System.out.println(sb.toString());//抓取的内容
            String context = sb.toString().replaceAll("&nbsp;", "");
            String regex = "<p class=\"article.*?/p>";  
            Pattern pt = Pattern.compile(regex);  
            Matcher mt = pt.matcher(context);
            Matcher mt2 = null;
            StringBuffer inserts = new StringBuffer("insert into qt_motto (mto_title, mto_content, mto_author) values");
            String content = null, title = null, author = null, res = null;
            while (mt.find()) {
            	//res = mt.group().replaceAll("(<p class=\"article.*?、)|<b.*?\">|<b.*?\">|</a.*?/p>", "::"); //匹配结果:|辱骂与恐吓不是战斗。|鲁迅|
            	regex = "<p class=\"article.*?<b>";//匹配内容
            	pt = Pattern.compile(regex);
            	mt2 = pt.matcher(mt.group());
            	while(mt2.find()){
            		content = mt2.group().replaceAll("(<p class=\"article.*?、)|<b>", "").trim();//内容
            	}
            	if(content != null){
            		if(content.replaceAll("[^\\x00-\\xff]", "01").length() > 100)
            			continue;
            		regex = "<b.*?/b>";//作者以及出处
                	pt = Pattern.compile(regex);
                	mt2 = pt.matcher(mt.group());
                	while (mt2.find()) {
                		res = mt2.group();
                		title = author = "";
                		if(res.contains("出处：")){
                			title = res.substring(res.indexOf("出处："), res.indexOf("</a>"));
                			title = title.replaceAll("出处：.*?>", "").trim();
                		}
                		if(res.contains("作者：")){
                			author = res.substring(res.indexOf("作者："), res.lastIndexOf("</a>"));
                			author = author.replaceAll("作者：.*?>", "").trim();
                		}
                		inserts.append("('"+title+"','"+content+"','"+author+"'),");
					}
            	}
            }  
            System.err.println(inserts.substring(0, inserts.length() - 1));
        } catch (IOException e) { 
            e.printStackTrace();
        }
	}
}
