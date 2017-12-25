package T;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.yfwl.yfgp.utils.PropertiesUtils;

public class HttpClientManyThread {
	private static void manyThreadHttpRequest() throws Exception {
		// httpclient线程池管理器
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		// 最大练级数
		connManager.setMaxTotal(200);
		// 设置每个路由连接数
		connManager.setDefaultMaxPerRoute(3);
		// 针对的主机
		HttpHost host = new HttpHost("http://localhost:8080/YFfg/zuhe/insertorder.do");
		// 每个路由映射的最大的连接数
		connManager.setMaxPerRoute(new HttpRoute(host), 5);
		// 创建httpclisent连接
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(connManager).build();
	

		String url="http://localhost:8080/YFfg/zuhe/insertorder.do";
		List<Map<String, String>> paramMaps = getOrderBookByProperties();
		int length = paramMaps.size();
		if (length > 0) {
			HttpContext context = null;
			HttpPost httpPost = null;
			List<BasicNameValuePair> param = null;
			for (Map<String, String> paramMap : paramMaps) {
				context = HttpClientContext.create();
				httpPost = new HttpPost(url);
				// 设置请求参数
				param = new ArrayList<BasicNameValuePair>();

				Iterator<Entry<String, String>> iter = paramMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					param.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
				param.add(new BasicNameValuePair("userid", "42557"));
				param.add(new BasicNameValuePair("token", "E36E4E7F720140F1BA4E6D952ED8E4142017071914552436925CA9"));
				httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
				ThreadGroupContext threadGroupContext = new ThreadGroupContext(
						httpClient, httpPost);
				threadGroupContext.start();
				threadGroupContext.join();
			}
		}
	}

	private static List<Map<String, String>> getOrderBookByProperties() {
		List<Map<String, String>> formParams = new ArrayList<Map<String, String>>();
		Properties pps = new Properties();
		try {
			pps.load(PropertiesUtils.class.getClassLoader()
					.getResourceAsStream("properties/thread.properties"));
			Enumeration enum1 = pps.propertyNames();
			Map<String, String> ParamMap = null;
			while (enum1.hasMoreElements()) {
				String strKey = (String) enum1.nextElement();
				String strValue = pps.getProperty(strKey);
				ParamMap = (Map<String, String>) JSON.parse(strValue);
				formParams.add(ParamMap);
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return formParams;
	}
	public static void main(String[] args) throws Exception {
		manyThreadHttpRequest();
		
	}
	
//	@Test
//	public void test4() throws Exception{
//		manyThreadHttpRequest();
//		
//	}
	
}
