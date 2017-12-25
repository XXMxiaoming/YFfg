package com.yfwl.yfgp.interceptor;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yfwl.yfgp.annotation.SensitivewordInterceptorAnnotation;

/**
 * 屏蔽敏感词拦截器
 * 
 * @author Luzhq
 *
 */
public class SensitivewordInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		SensitivewordInterceptorAnnotation annotation = method
				.getAnnotation(SensitivewordInterceptorAnnotation.class);

		if (annotation != null) {

			// System.out.println("你遇到了：@SensitivewordInterceptorAnnotation");

			/**
			 * 获取传过来的参数，参数值用SensitivewordFilter过滤，替换敏感词
			 * 注：request.getParameterMap()拿到的Map是 Map<String,String[]>，值为数组，一个Key对应多个值
			 */
			@SuppressWarnings("unchecked")
			Map<String, String[]> map = request.getParameterMap();
			Set<Entry<String, String[]>> set = map.entrySet();
			Iterator<Entry<String, String[]>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String[]> entry = it.next();
				String key = entry.getKey();
				System.out.println("KEY:" + entry.getKey());
				for (String i : entry.getValue()) {
					SensitivewordFilter sensitivewordFilter = new SensitivewordFilter();
					String newI = sensitivewordFilter.replaceSensitiveWord(URLDecoder.decode(i, "utf-8"),
							1, "*");
					System.out.println(newI);
					request.setAttribute(key, URLEncoder.encode(newI, "utf-8"));
				}
			}
			// System.out.println("没有通过拦截" );
		}
		// System.out.println("通过拦截" );
		return true;
	}

}
