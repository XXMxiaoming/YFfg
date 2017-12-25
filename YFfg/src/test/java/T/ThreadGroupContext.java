package T;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class ThreadGroupContext extends Thread {
	private final CloseableHttpClient httpClient;
	private final HttpContext context;
	private final HttpPost httpPost;
	
	public ThreadGroupContext(CloseableHttpClient httpClient, HttpPost httpPost) {
		this.httpClient = httpClient;
		this.context = HttpClientContext.create();
		this.httpPost = httpPost;

	}

	@Override
	public void run() {
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost,
					context);
			try {
				HttpEntity entity = response.getEntity();
				System.out.println("结果集转string串____:"
						+ EntityUtils.toString(entity));
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
