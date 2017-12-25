package T;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;




import org.json.JSONObject;

import com.sun.jdi.Method;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.SortByMap;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
public class te {
	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//		String str = "java12345";
//		String ret = null;
//		ret = new BASE64Encoder().encode(str.getBytes());
//		System.out.println("加密前:"+str+" 加密后:"+ret);
//		str = "amF2YTEyMzQ1";
//		try {
//			ret = new String(new BASE64Decoder().decodeBuffer(str));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	    System.out.println("解密前:"+str+" 解密后:"+ret);
		//
		System.out.println(Integer.parseInt(("54565")));
		//得到前一天的时间
//		Calendar ac=Calendar.getInstance();
//		ac.add(Calendar.DATE, -1);
//		String yesterday=new SimpleDateFormat("yyyyMMdd").format(ac.getTime());
//		System.out.println(Integer.parseInt(yesterday));

//		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date datenow = new Date();
//		String dateString = dateSdf.format(datenow);
//		System.out.println(dateString.substring(0, 10));
//		
//		test t=new test();
		
		//System.out.println(t.init());

		//当前日期时间
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
		
		//test1();
		//hah();
		//h();
		//a();
		//c();
		//d();
//		Calendar c=Calendar.getInstance();
//		c.add(Calendar.DATE, -1);
//		String date=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
//		System.out.println(Integer.parseInt(yesterday));
		
		System.out.println(te.class.getResource(""));
		System.out.println(te.class.getResource("/"));
		
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");

		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		System.out.println(week);
		SimpleDateFormat dateSdf2 = new SimpleDateFormat(
				"yyyy-MM-dd");
		String dateString2 = dateSdf2.format(datenow);
		System.out.println(dateString2);
		
		Calendar c = Calendar.getInstance();
		int week2 = c.get(Calendar.DAY_OF_WEEK);
		System.out.println("week"+week2);
		
		  int j = 3;  
	        for (int i = 0; i < 5; i++) { 
	        	  if (i == j)  {  
	  	            continue;  
	  	         }
	        	  System.out.println("i = " + i);  
	        }
	      
	            
	  
	    System.out.println("循环结束");  
	    Date now = new Date();
	    System.out.println(now);
	
}
	
	public static void test1(){
		String str = "{\"0\":\"zhangsan\",\"1\":\"lisi\",\"2\":\"wangwu\",\"3\":\"maliu\"}";
		com.alibaba.fastjson.JSONObject js=	(com.alibaba.fastjson.JSONObject) (com.alibaba.fastjson.JSONObject.parse(str));
		com.alibaba.fastjson.JSONObject js1=(com.alibaba.fastjson.JSONObject.parseObject(str));
		JSONObject js2=new JSONObject(str);
		System.out.println(js);
		System.out.println(js1);
		System.out.println(js2);
	}

	public static JSONObject doSent(String url,Map<String,String> map){
		JSONObject js=null;
		DefaultHttpClient httpclient=new DefaultHttpClient();
		try {
			HttpPost post=new HttpPost(url);
			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if(map!=null){
			Iterator<Entry<String, String>>	it=  map.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, String> entry= it.next();
				param.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
			}
			post.setEntity(new UrlEncodedFormEntity(param,"utf-8"));
			 HttpResponse response=httpclient.execute(post);
			 HttpEntity entity = null;
			 try {
				 entity= response.getEntity();
				 if(entity!=null){
					 String res=entity.toString();
					 js=new JSONObject(res);
				 }
				 
				 
			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				if (entity != null) {
					entity.getContent().close();
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return js;
		
		
	}
	
	
	
	public Object invoke(Object proxy, Method method, Object[] args){
		return null;
		
	}
	
	
	
	public static List hah(){
		int a=2147483647;
		System.out.println(a);
		List<List<String>> hh=new ArrayList<List<String>>();
		List<List<String>> ha=new ArrayList<List<String>>();
		List<String> ai=new ArrayList<String>();
		List<String> ao=new ArrayList<String>();
		ai.add("aa");
		ai.add("bushiba");
		ao.add("gf");
		ao.add("gfgfg");
		hh.add(ai);
		hh.add(ao);
	
		System.out.println(hh);
		return hh;
		
	}
	
	public static void h(){
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list=new ArrayList<String>();
		List<String> list2=new ArrayList<String>();
		int j = 10;
		for (int i = 1; i <= 10; i++) {
			list.add(j+"%");
			j += 10;
		}
		int k = 0;
		for (int i = 0; i <= 6; i++) {
			list2.add(k+"%");
			k += 5;
		}
		map.put("income", list);
		map.put("loss", list2);
		System.out.println(map);
	}
	
	public static void a(){
		
	Map<String, String> map=new HashMap<String, String>();
	String time = String.valueOf(new Date().getTime()).substring(0, 10);
	map.put("ctl", "api");
	map.put("ctl", "api");
	map.put("fage_id", "81");
	map.put("act", "bind_identification_and_bank");
	map.put("type_identification=", "credit_identificationscanning");
	map.put("real_name", "谢晓明");
	map.put("idno", "43138199309020876");
	map.put("region_lv1", "1");
	map.put("region_lv2", "2");
	map.put("region_lv3", "3");
	map.put("region_lv4", "4");
	map.put("bankzone", "哈哈哈");
	map.put("bankcard", "464132131545465");
	map.put("time", time);
	System.out.println(SortByMap.sort(map));

	}

	

	
	public static void d() throws UnsupportedEncodingException, NoSuchAlgorithmException{
	String s=MD5Util.getDigest("血");
	System.out.println(s);
	}
	
	
}


