package com.mypro.model.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 数据字典表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_dict", pkName = "dict_no")
public class SysDict extends BaseModel<SysDict> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysDict dao = new SysDict();
	public static final String all = "dict_no,dict_fno,dict_name,dict_desc,dict_state,dict_scort,dict_seq,dict_level,dict_icon,dict_param";
	public static final String simple = "dict_no,dict_name,dict_level,dict_icon,dict_param";
	public static final String noandname = "dict_no,dict_name";
	public static final String simplewithb = "b.dict_no,b.dict_fno,b.dict_name,b.dict_level,b.dict_icon,b.dict_param";
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 * 字典树状集合
	 * @param dicts 所有的字典集合
	 * @param dict 父级菜单
	 * @return
	 */
	public static List getDictTree(List<SysDict> dicts, SysDict dict){
		if(dicts == null)
			return null;
		List<Map> res = new ArrayList<Map>();
        Map map = null;
		for (SysDict d : dicts) {
			if(dict == null){
				if(StringUtils.isNotEmpty(d.getStr("dict_fno")))//这是一级菜单
					continue;
			}else{
				if(!d.get("dict_fno","").equals(dict.get("dict_no")))
					continue;
			}
			map = new HashMap();
			map.put("id", d.get("dict_no"));
	    	map.put("name", d.get("dict_name"));
	    	map.put("level", d.get("dict_level"));
	    	map.put("count", d.get("art_count", 0));//用于统计在某一类型下文章的数量
	    	map.put("open", true);
        	map.put("children", getDictTree(dicts, d));
        	res.add(map);
		}
		return res;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getFrontOptions(List<Map> dicts, String dict_no){
    	String[] emsp = new String[]{"4","6","24","36"};
    	StringBuffer str = new StringBuffer();
    	List<Map> children = null;
    	Map dict = null;
    	String id = null, selected = "";
		for(int i = 0, l = dicts.size(); i < l; i++){
			selected = "";
			dict = dicts.get(i);
			id = (String) dict.get("id");
			children = (List<Map>) dict.get("children"); 
			if( children.size() > 0){
				if(id.equals("article")){
					str.append(getFrontOptions(children, dict_no));
					continue;
				}
				str.append("<optgroup label=\""+dict.get("name")+"\">");
				str.append(getFrontOptions(children, dict_no)).append("</optgroup>");
			}else{
				if(dict_no != null && dict_no.equals(id))
					selected = "selected";

				str.append("<option "+selected+" value=\""+id+"\" style=\"padding-left:"+emsp[(int) dict.get("level")]+"px;\">"+dict.get("name")+"</option>");
			}
		}
		return str.toString();
    };
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getFrontLis(List<Map> dicts, String dict_no){
    	String[] emsp = new String[]{"4","10","24","36"};
    	StringBuffer str = new StringBuffer();
    	List<Map> children = null;
    	Map dict = null;
    	String id = null, selected = "";
		for(int i = 0, l = dicts.size(); i < l; i++){
			selected = "";
			dict = dicts.get(i);
			id = (String) dict.get("id");
			children = (List<Map>) dict.get("children"); 
			if(dict_no != null && dict_no.equals(id))
				selected = "selected";
			if( children.size() > 0){
				if(id.equals("article")){
					str.append(getFrontLis(children, dict_no));
					continue;
				}
				str.append("<li class=\"tm-opt tm-p "+selected+"\" data-no=\""+id+"\">"+dict.get("name")+"</li>");
				str.append(getFrontLis(children, dict_no));
			}else{
				str.append("<li class=\"tm-opt "+selected+"\" data-no=\""+id+"\" style=\"padding-left:"+emsp[(int) dict.get("level")]+"px;\">"+dict.get("name")+"</li>");
			}
		}
		return str.toString();
    };
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getFrontZoneLi(List<Map> dicts){
    	String[] emsp = new String[]{"0","0","24","36"};
    	StringBuffer str = new StringBuffer();
    	List<Map> children = null;
    	Map dict = null;
    	String id = null;
		for(int i = 0, l = dicts.size(); i < l; i++){
			dict = dicts.get(i);
			id = (String) dict.get("id");
			children = (List<Map>) dict.get("children"); 
			if( children.size() > 0){
				if(id.equals("article")){
					str.append(getFrontZoneLi(children));
					continue;
				}
				str.append("<li class=\"cat-item\" style=\"font-weight:bold; padding-left:"+emsp[(int) dict.get("level")]+"px;\">"+dict.get("name")+"</li>");
				str.append(getFrontZoneLi(children));
			}else
				str.append("<li class=\"cat-item\" style=\"padding-left:"+emsp[(int) dict.get("level")]+"px;\"><a href=\"#\" title=\"title\">"+dict.get("name")+"("+dict.get("count")+")"+"</a></li>");
		}
		return str.toString();
    };
}
