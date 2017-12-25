package com.yfwl.yfgp.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
	
	/**
	 * 最大连接数
	 */
	private final static int MAX_TOTAL_CONNECTIONS = 500;
	/**
	 * 每个路由最大连接数
	 */
	private final static int MAX_ROUTE_CONNECTIONS = 80;

	/**
	 * 超时时间
	 */
	private final static int CONNECTION_TIME_OUT = 15000;

	/**
	 * READ时间
	 */
	private final static int READ_TIME_OUT = 15000;

	private static RequestConfig requestConfig;
	private static PoolingHttpClientConnectionManager connectionManager;
	
	static {
		connectionManager = new PoolingHttpClientConnectionManager();
		// 设置最大连接数
		connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		// 设置每个路由最大连接数
		connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
		// 设置请求和传输超时时间
		requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIME_OUT).setConnectTimeout(CONNECTION_TIME_OUT).build();
	}
	
	private static HttpClient getHttpClient() {
//		return HttpClients.createMinimal(connectionManager);
		return HttpClients.createMinimal();
	}

	public static HttpPost getPostRequest(String url) {
		HttpPost request = new HttpPost(url);
		request.setConfig(requestConfig);
		return request;
	}

	public static HttpGet getGetRequest(String url) {
		HttpGet request = new HttpGet(url);
		request.setConfig(requestConfig);
		return request;
	}

	/**
	 * 得到response内容
	 * 
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	public static String getResponseResult(HttpResponse response, String defaultCharset) throws IllegalStateException, IOException {
		HttpEntity entity = response.getEntity();
		try {
			if (entity != null) {
				return EntityUtils.toString(entity, defaultCharset);
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}finally {
			if (entity != null) {
				entity.getContent().close();
			}
		}
		return null;
	}
	
	public static HttpPost setPostParams(HttpPost request,HashMap<String,String> params){
		List<NameValuePair> nvps = Lists.newArrayList();
		for(Entry<String,String> kv:params.entrySet()){
			nvps.add(new BasicNameValuePair(kv.getKey(), kv.getValue()));
		}
        try {
			request.setEntity(new UrlEncodedFormEntity(nvps));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return request; 
	}

	public static HttpResponse execute(HttpRequestBase request) throws ClientProtocolException, IOException {
		HttpClient client = getHttpClient();
		return client.execute(request);
	}

	public static String execute(HttpRequestBase request, String defaultCharset) throws ClientProtocolException, IOException {
		HttpClient client = getHttpClient();
		return getResponseResult(client.execute(request), defaultCharset);
	}
}
