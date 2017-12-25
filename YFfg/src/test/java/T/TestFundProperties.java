package T;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;




import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yfwl.yfgp.utils.PropertiesUtils;

public class TestFundProperties {
	
	public static void main(String[] args) {
		List<Map<String, String>> formParams = new ArrayList<Map<String, String>>();
		Properties pps = new Properties();
		try {
			pps.load(new InputStreamReader(PropertiesUtils.class.getClassLoader()
					.getResourceAsStream("properties/fund.properties"),("utf-8")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Enumeration enum1 = pps.propertyNames();
		Map<String, String> ParamMap = null;
		while (enum1.hasMoreElements()) {
			String strKey = (String) enum1.nextElement();
			String strValue = pps.getProperty(strKey);
	
		JSONObject jsStr = JSONObject.parseObject(strValue);
		String fid=	jsStr.getString("fid");
		String name=jsStr.getString("name");
		System.out.println(fid);
		System.out.println(name);
			System.out.println(strValue);
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> maps = (Map<String, Object>) JSON
					.parseObject(strValue);
			for (Object map3 : maps.entrySet()) {
				map.put(((Map.Entry) map3).getKey().toString(),
						((Map.Entry) map3).getValue());
				System.out.println();
			}
			
//			ParamMap = (Map<String, String>) JSON.parse(strValue);
//			formParams.add(ParamMap);
//			System.out.println(formParams);
		}
		
		
	}
	
	

}
