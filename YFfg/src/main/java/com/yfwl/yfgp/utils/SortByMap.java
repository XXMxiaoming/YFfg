package com.yfwl.yfgp.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SortByMap {
	public static String sort(Map<String, String> map) {

		Map<String, String> resultMap = sortMapByValue(map);	 //按Value进行排序
		String text="";
		for (Map.Entry<String, String> entry : resultMap.entrySet()) {
			//System.out.println( entry.getValue());
			text+=entry.getValue();
		}
		return text;
	}
		
		
		public static Map<String, String> sortMapByValue(Map<String, String> oriMap) {
			if (oriMap == null || oriMap.isEmpty()) {
				return null;
			}
			Map<String, String> sortedMap = new LinkedHashMap<String, String>();
			List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(
					oriMap.entrySet());
			Collections.sort(entryList, new MapValueComparator());
			Iterator<Map.Entry<String, String>> iter = entryList.iterator();
			Map.Entry<String, String> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
			return sortedMap;
		}
}
