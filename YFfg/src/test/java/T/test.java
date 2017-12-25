package T;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.ControllerTest;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.Time;


@Controller
public class test {
	
	@Resource
	GroupService groupService;
	@Resource
	TokenService tokenService;
	@Resource
	UserService userService;
	@Resource
	CashService cashService;
	private static int userNumber  = 0 ;  
    
    public test(){  
        userNumber ++;  
    }  
	public static void main(String[] args) {
		// String s="ahahah哈哈哈哈哈啊哈哈哈哈哈0000001";
		// for (int i = 0; i < s.length(); i++) {
		// System.out.println(Character.isDigit(s.charAt(i)));
		// }
		// SimpleDateFormat xgsdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// Date datenow = new Date();
		// String xgtime = xgsdf.format(datenow);
		// System.out.println(xgtime);
//		int hash[];
//		hash = new int[3*65536];
//		hash[98]=3;
//		char c1 = 0;
//		char c2 = 0;
//		char c3 = 0;
//		char c4 = 0;
//		int k=0;
//		String s = "哈哈不是";
//		s.charAt(0);
//		s.charAt(1);
//		s.charAt(2);
//		s.charAt(3);
//		c1 = s.charAt(0);
//		c2 = s.charAt(1);
//		c3 = s.charAt(2);
//		c4 = s.charAt(3);
//		k = (int)c1+(int)c2+(int)c3+(int)c4;
//		System.out.println(k);
//		System.out.println(hash[k]);
////		int a[]=new int[3];
////		a[1]=2;
////		System.out.println(hash[a[1]]);
		
		
//		Test test=new Test();
//		test.setSex("10");
//		System.out.println(test.getSex());
		
//		Date datenow = new Date();
//		System.out.println(datenow);
//		SimpleDateFormat dateSdf2 = new SimpleDateFormat(
//				"yyyy-MM-dd");
//		String dateString2 = dateSdf2.format(datenow);
//		System.out.println(dateString2);
//		 int a=5;
//		 int b=0;
//		 int d=4;
//		 int c=0;    
//		  for(int i=0;i<=10;i++){
//			  c=d;
//			  try {
//				 c=a/b;
//			} catch (Exception e) {
//				// TODO: handle exception
//				 d=3;
//				 b=1;
//			}
//			 System.out.println("a"+a);
//			 System.out.println("c"+c);
//			  
//		  }
//		String stockStr ="002791.SZ";
//		String token="0CD6AD0EF29E4A1E910F7CF79A84A5CB2017091317130736925CA9";
//		String resultStr = GetHSTokenUtils.getReal(stockStr, token);
//		JSONObject jsonData = new JSONObject(resultStr);
//		System.out.println(jsonData);
//		String s="fdsf";
//		Double c=Double.parseDouble(s);
//		if(c!=null){
//			System.out.println(c);
//		}else{
//			System.out.println("jglkfds");
//		}
//		Calendar c = Calendar.getInstance();
//		int week = c.get(Calendar.DAY_OF_WEEK);
//		int hour = c.get(Calendar.HOUR_OF_DAY); 
//		int minute = c.get(Calendar.MINUTE);
//		
//		SimpleDateFormat dateSdf =   new SimpleDateFormat("yyyyMMdd");
//		Date datenow = new Date();
//		String dateString = dateSdf.format(datenow);
//
//		System.out.println(week);
		
//		String stockid = "002194";
//		String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
//		String result = GetHSTokenUtils.getBuySallPoint(stock);
//		String real = GetHSTokenUtils.getReal(stock, "5B3DAF3043224986875DE307D3D098872017092718055836925CA9");
//		System.out.println(result);
//		System.out.println(real);
		
//		int i=2;
//		for (int j = 0; j < 10; j++) {
//			
//			switch(i){
//			case 2:
//				if(j>3){
//					break;
//				}
//				System.out.println("i"+i);
//				
//				
//			}
//			System.out.println(j);
//		}
//		Calendar c=Calendar.getInstance();
//		long a=c.getTimeInMillis();
//		long d=System.currentTimeMillis();
//		System.out.println(a);
//		System.out.println(d);
//		for (int i = 0; i < 10000000; i++) {
//			i++;
//			if(i>500000){
//				break;
//			}
//		}
//		long b=c.getTimeInMillis();
//		long e=System.currentTimeMillis();
//		System.out.println(e);
//		System.out.println(b);
//		//System.out.println(b-a);
//		System.out.println(e-d);
//		//long d=System.currentTimeMillis();
//	 Date nowTime = new Date(System.currentTimeMillis());
//		  //Date nowTime=new Date("1506681759828");
//		  SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		  String retStrFormatNowDate = sdFormatter.format(nowTime);
//		  System.out.println(retStrFormatNowDate);
		  
		 //   String stockid = "000725";
			//String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
			String stock ="000048.SZ";
			String result = GetHSTokenUtils.getBuySallPoint(stock);
			String real = GetHSTokenUtils.getReal(stock, "E496A49CEE0543E7888352CB68C72F642017100909400836925CA9");
			JSONObject realObject = new JSONObject(real);
			
			JSONObject jsonObject = new JSONObject(result);
			JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
			int len = candle.getJSONArray(stock).length();
			JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
			int buyorsall = stockArray.getInt(1);
			int time =stockArray.getInt(0);
			System.out.println(result);
			System.out.println(stockArray);
			System.out.println(buyorsall);
			System.out.println(time);
		
//		int a=1;
//		for(int i=1;i<5;i++){
//			try {
//				a--;
//				System.out.println(i/a);
//				
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//				//continue;
//			}
//			
//		}
//			Date now = new Date();
//			Integer time = 20171015;
//			//int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//			
//			try {
//				Date	date = sdf.parse(time.toString());
//				System.out.println(date);
//				int days = Time.daysBetween(date, now);
//				if((days <= 1 && days >= -1) || true) {	
//					System.out.println("gjdsafkgjfd");
//				}
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//		Date datenow = new Date();
//		System.out.println(datenow);
			
//			String a="a"+"b";
//			String c="ab";
//			if(a==c){
//				System.out.println("dsaf");
//			}
//			int i=10;
//			i=Integer.parseInt(("00"+i).toString());
//			System.out.println(i);
//
//			Calendar ac = Calendar.getInstance();
//			ac.add(Calendar.DATE, -1);
//			String yesterday = new SimpleDateFormat("yyyyMMdd")
//					.format(ac.getTime());
//			System.out.println(yesterday);
//			
//			SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
//			Date datenow = new Date();
//			String dateString = dateSdf.format(datenow);
//			System.out.println(dateString);
//			
//			Calendar cl = Calendar.getInstance();
//			cl.setTime(new Date());
//			int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
//			System.out.println(week);
			}
}