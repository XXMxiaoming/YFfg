package com.yfwl.yfgp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;



public class Test {
	
	//private static final Logger logger = LoggerFactory.getLogger(Test.class); 
	
	
	public static void main(String[] args) throws IOException {
		
		Date timeStart = new Date();
		Calendar c = Calendar.getInstance();
	    c.setTime(timeStart);
	    c.add(Calendar.MONTH, 6);
		Date timeExpire = c.getTime();
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	
		System.out.println(sdf.format(timeStart));
		System.out.println(sdf.format(timeExpire));
		
		
		
		
		
		
		//getURLContent();
		
		/*for(int i=0;i<15;i++){
			int index = (int)((Math.random())*4);
			System.out.println(index);
		}*/
		
		/*Logger logger = Logger.getLogger(Test.class);
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%-d{yyyy-MM-dd HH:mm:ss} [ %t:%r ] - [ %p ] %m%n");
		DailyRollingFileAppender appender = new DailyRollingFileAppender(layout,"d:/logs/output1_","yyyy-MM-dd'.txt'");
		logger.addAppender(appender);*/
		/*Logger logger = Test.getLogger("d:/logs/yfgp/output1_");
		logger.debug("test debug222222");*/
		
		/*Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK)-1;
		System.out.println(week);*/
		
		/*String s = "优化组合";
		if(s.contains("优化组合")){
			System.out.println("hhhhhhh");
		}*/
	}

	public static Logger getLogger(String filePath){
		Logger logger = Logger.getLogger(Test.class);
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%-d{yyyy-MM-dd HH:mm:ss} [ %t:%r ] - [ %p ] %m%n");
		DailyRollingFileAppender appender;
		try {
			appender = new DailyRollingFileAppender(layout,filePath,"yyyy-MM-dd'.txt'");
			logger.addAppender(appender);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
	
	private static String getURLContent() {
		URL url = null;
		/** http连接 */
		HttpURLConnection httpConn = null;
		/**//** 输入流 */
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			url = new URL("http://120.24.208.86:30555/apk/yfStock.properties");
			in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
			String str = null;
			while ((str = in.readLine()) != null) {
				//sb.append(str).append("\n");
				String[] s1 = str.split(" = ");
				map.put(s1[0], s1[1]);
			}
		} catch (Exception ex) {

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			}
		}
		String result = sb.toString();
		return result;
	}

}