package com.yfwl.yfgp.easemodrest.comm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesUtils
 * 
 * @author Lynch 2014-09-15
 *
 */
public class PropertiesUtils {

	public static Properties getProperties() {

		Properties p = new Properties();

		try {
			InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(
					"conf/RestAPIConfig.properties");
						
			p.load(inputStream);
			 /*String path = PropertiesUtils.class.getClass().getResource("/").getPath();
			 System.out.println(path);
			 path = path.substring(1, path.indexOf("target"))+"src/main/resources/conf/";
			 p.load(new FileInputStream(path + "RestAPIConfig.properties"));
			 
			// /yfgp/src/main/resources/conf/RestAPIConfig.properties

			 
			 System.out.println("APPKEY="+p.getProperty("APPKEY"));
			 System.out.println("APP_CLIENT_ID="+p.getProperty("APP_CLIENT_ID"));
			 System.out.println("APP_CLIENT_SECRET="+p.getProperty("APP_CLIENT_SECRET"));*/

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return p;
	}

}
