/**
 * 
 */
package com.mypro.common.tools;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author jack.li
 *
 */
public class BeanUtil {

	public static Map<String, Object> bean2map(Object bean, String[] exclude) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> excludeProperties = new ArrayList<String>();
		excludeProperties.add("class");
		excludeProperties.addAll(Arrays.asList(exclude));
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bean);
		for(int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
			String name = propertyDescriptor.getName();
			Object value = propertyDescriptor.getReadMethod().invoke(bean);
			if(!excludeProperties.contains(name)) {
				map.put(name, value);
			}
		}
		System.out.println("total:"+(map.size()));
		return map;
	}
	
	public static Map<String, Object> bean2map(Object bean) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return bean2map(bean, new String[]{});
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

	}
	
}
