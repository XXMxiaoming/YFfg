package com.yfwl.yfgp.posttestutils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;



import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;

public class ControllerTest {
	private static Log log = LogFactory.getLog(ControllerTest.class); 
	
	//protected static String host = "http://120.76.192.128:30555/YFfg/";
	protected static String host = "https://www.fage18.com/YFfg/";
	//protected static String host = "http://localhost:8080/YFfg/";

	// protected String host = "http://192.168.1.103:8080/yfgp/";
	// protected String host = "http://120.24.208.86:30555/yfgp/";

	/**
	 * http post请求,ContentType类型：application/json
	 * 
	 * @param url
	 *            请求URL
	 * @param map
	 *            请求参数
	 * @return
	 */
	public String doJsonPost(String url, HashMap<String, String> map) {

		try {
			HttpPost post = new HttpPost(url);

			// 设置访问的类型为：application/json
			ContentType type = ContentType.APPLICATION_JSON;

			// 设置请求参数
			StringEntity entity = new StringEntity(new Gson().toJson(map), type);
			post.setEntity(entity);

			// 执行post请求
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String doPost(String url, HashMap<String, String> map) {
		try {
			HttpPost post = new HttpPost(url);

			// 设置请求参数
			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					param.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));

			// 执行post请求
			// System.out.println("POST前");
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 自定义头部post请求
	 * 
	 * @param url
	 * @param map
	 * @param headMap
	 * @return
	 */
	public String doPost(String url, HashMap<String, String> map,
			HashMap<String, String> headMap) {
		try {
			HttpPost post = new HttpPost(url);

			// 设置头部参数
			if (headMap != null) {
				Iterator<Entry<String, String>> iter = headMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置请求参数
			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					param.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));

			// 执行post请求
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 带文件上传post请求
	 * 
	 * @param url
	 * @param map
	 * @param fileMap
	 * @param headMap
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doPost(String url, HashMap<String, String> map,
			HashMap<String, File> fileMap, HashMap<String, String> headMap) {

		try {
			HttpPost post = new HttpPost(url);

			// 设置头部参数
			if (headMap != null) {
				Iterator<Entry<String, String>> iter = headMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置请求参数
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			entityBuilder.setCharset(Charset.forName(HTTP.UTF_8));
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					entityBuilder.addTextBody(entry.getKey(), entry.getValue());
				}
			}

			// 设置文件
			if (fileMap != null) {
				Iterator<Entry<String, File>> iter = fileMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, File> entry = (Entry<String, File>) iter
							.next();
					File file = entry.getValue();
					ContentBody contentBody = new FileBody(file);
					entityBuilder.addPart(entry.getKey(), contentBody);
				}
			}
			HttpEntity entity = entityBuilder.build();
			post.setEntity(entity);
			// 执行post请求
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
//	public static String doGet3(String url, String queryString) {
//		String response = null;
//		HttpClient client = new HttpClient();
//		HttpMethod method = new GetMethod(url);
//		try {
//			if (StringUtils.isNotBlank(queryString))
//				method.setQueryString(URIUtil.encodeQuery(queryString));
//			client.executeMethod(method);
//			if (method.getStatusCode() == HttpStatus.SC_OK) {
//				response = method.getResponseBodyAsString();
//			}
//		} catch (URIException e) {
//			log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
//		} catch (IOException e) {
//			log.error("执行HTTP Get请求" + url + "时，发生异常！", e);
//		} finally {
//			method.releaseConnection();
//		}
//		return response;
//	}
}
