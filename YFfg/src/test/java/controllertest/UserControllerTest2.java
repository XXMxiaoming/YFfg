package controllertest;

import java.util.HashMap;

import org.junit.Test;

import sun.misc.BASE64Encoder;

import com.yfwl.yfgp.posttestutils.ControllerTest;


public class UserControllerTest2 extends ControllerTest{

	@Test
	public void test4(){
		String url = host+"user2/insertUser2.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userName", "小明123");
		map.put("user_pwd", "xxm1234567");
		map.put("mobile", "15220138713");
		map.put("verify", "6188");
		System.out.println(doPost(url, map));
	}
//	
//	
//	@Test
//	public void test28(){
//		String url = host+"user/updatePassword.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("phone", "15220138713");
//		map.put("password", "xxm123456");
//		map.put("verify", "4788");
//		
//		//map.put("", "");
//		System.out.println(doPost(url, map));
//	}
//	
//	
//	//{"msg":"操作失败","data":"149784258015220138713apiget_register_verify_codeYx3V27g4SckNJ1Zk非法请求149784258015220138713apiget_register_verify_codeYx3V27g4SckNJ1Zk","status":1}
//
//	@Test
//	public void test27(){
//		String url = host+"validateCode/validateCodeWhenZHMM.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("phone", "15220138713");
//		map.put("randomNum", "4788");
//		//map.put("", "");
//		System.out.println(doPost(url, map));
//	}
//	
//	
//	@Test
//	public void test23(){
//		String url = host+"validateCode/validateCodeWhenRegister.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		
//		map.put("phone", "15220138713");
//		map.put("randomNum", "9938");
//		//map.put("", "");
//		System.out.println(doPost(url, map));
//	}
//	
//	//登录
	@SuppressWarnings("restriction")
	@Test
	public void test2(){
		String url = host+"user2/login2.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("loginName", "15220138713");
		map.put("password", new BASE64Encoder()
		.encode("xxm123456".getBytes()));
		//本地
		//map.put("password", "xxm123456");
		System.out.println(doPost(url, map));
	}
//	
////	@Test
////	public void test20(){
////		String url = host+"user/sendSmsUpdatePassword.do";
////		HashMap<String, String> map = new HashMap<String, String>();
////		map.put("mobile", "15220138713");
////		System.out.println(doPost(url, map));
////	}
//	@Test
//	public void test30(){
//		String url = host+"gentou/AllMethod.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("phone", "15220138713");
//		map.put("name", "非主流");
//		map.put("id", "33");
//		System.out.println(doPost(url, map));
//	}
//	
//	
//	
//	@Test
//	public void test21(){
//		String url = host+"user2/updatePwd.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("userId", "42531");
//		map.put("oldPwd", "xxm12");
//		map.put("password", "xxm12378");
//		
//		System.out.println(doPost(url, map));
//	}
//	
//	
	@Test
	public void test3(){
		String url=host+"zuhe/create.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", "81");
		map.put("token", "E9D8831649FF4922A262581055408CE22017090811105536925CA9");
		//map.put("gname", "小明");
		System.out.println(doPost(url, map));
	}
//	
//	@Test
//	public void test5(){
//		String url=host+"zuhe/queryzuhe.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("userid", "10011");
//		//map.put("token", "1");
//		System.out.println(doPost(url, map));
//	}
//	
//	@Test
//	public void test6(){
//		String url=host+"zuhe/account.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("useridString", "42477");
//		map.put("gid", "13");
//		map.put("token", "18A05C3D2FB043D884DC2641B5B3EAE92017072614433236925CA9");
//		System.out.println(doPost(url, map));
//	}
//	
//	
	@Test
	public void test7(){
		String url=host+"zuhe/insertorder.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid","81");
		map.put("token", "0CD6AD0EF29E4A1E910F7CF79A84A5CB2017091317130736925CA9");
		map.put("gid", "46");
		map.put("stock", "002305");
		map.put("price", "14");
		map.put("vol", "1");
		map.put("act", "1");
		//System.out.println(map);
		System.out.println(doPost(url, map));
	}
//	@Test
//	public void test8(){
//		String url=host+"zuhe/queryscore.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//	
//		map.put("gid", "13");
//	
//		System.out.println(doPost(url, map));
//	}
//	
//	
//	@Test
//	public void test10(){
//		String url=host+"zuhe/checktoken.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("userid", "10011");
//		map.put("token", "1");
//		//map.put("gname", "5组");
//		System.out.println(doPost(url, map));
//	}
	
	
	@Test
	public void test11(){
		String url=host+"future/getstock.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userId", "126");
		System.out.println(doPost(url, map));
	}
	
	@Test
	public void test29(){
		String url=host+"future/getstock2.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("codeStr", "399001.SZ,000001.SS,399006.SZ,"
				+ "002825.SZ ,000019.SZ ,600018.SS,000048.SZ ,300059.SZ ,000025.SZ ,000063.SZ,"
				+ "000040.SZ ,000002.SZ ,600009.SS,000034.SZ ,600033.SS,600519.SS,000011.SZ ,601939.SS,000043.SZ ,000068.SZ,"
				+ "000060.SZ ,601117.SS,000022.SZ ,000005.SZ ,002176.SZ ,600006.SS,000037.SZ ,000016.SZ ,300004.SZ,"
				+ "000046.SZ ,000061.SZ ,000027.SZ ,000004.SZ ,601398.SS,000032.SZ ,000055.SZ ,"
				+ "600031.SS,600000.SS,000018.SZ ,000066.SZ ,000049.SZ ,000007.SZ ,600011.SS,600008.SS,000035.SZ,"
				+ "300002.SZ ,600755.SS,000010.SZ ,300193.SZ ,000096.SZ ,002305.SZ ,"
				+ "000021.SZ ,000006.SZ ,000029.SZ ,000038.SZ ,600718.SS,000030.SZ ,300003.SZ ,000070.SZ ,300201.SZ ,000001.SZ ,"
				+ "601288.SS,000026.SZ ,000099.SZ ,000572.SZ ,000009.SZ ,000050.SZ ,002004.SZ ,"
				+ "000012.SZ ,601988.SS,000017.SZ ,000042.SZ ,300009.SZ ,000065.SZ ,000090.SZ ,"
				+ "000023.SZ ,000008.SZ ,600007.SS,600012.SS,000078.SZ ,600570.SS,000059.SZ ,002005.SZ ,000036.SZ ,"
				+ "000045.SZ ,000939.SZ ,000039.SZ ,002194.SZ ,000062.SZ ,600004.SS,600052.SS,000020.SZ ,"
				+ "000031.SZ ,000056.SZ ,000028.SZ ,002862.SZ ,300708.SZ ,000014.SZ ,000962.SZ ,002650.SZ ");
		System.out.println(doPost(url, map));
	}
	
	@Test
	public void test229(){
		String url=host+"future/getstock2.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("codeStr", "300006.SZ,000002.SZ");
		System.out.println(doPost(url, map));
	}
	
	
	@Test
	public void test12(){
		String url=host+"future/insertstock2.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userId", "81");
		map.put("stockCode","000001.SZ");
		System.out.println(doPost(url, map));
	}
	@Test
	public void test31(){
		String url=host+"future/insertstock.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userId", "42535");
		map.put("codeStr","000001.SZ,000002.SZ,000");
		map.put("type", "1");
		System.out.println(doPost(url, map));
	}
	
	@Test
	public void test13(){
		String url=host+"future/deletestock2.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userId", "10021");
		map.put("stockCode","000005.SZ&&世纪星合");
		System.out.println(doPost(url, map));
	}
	
	@Test
	public void test14(){
		String url=host+"banner/getBanner.do";
		HashMap<String, String> map = new HashMap<String, String>();
		System.out.println(doPost(url, map));
	}
	
	
	//预期智能配置
	@Test
	public void test115(){
		String url=host+"incomeLoss/expectIncomeLoss.do";
		HashMap<String, String> map = new HashMap<String, String>();
		System.out.println(doPost(url, map));
	}
	
	
	//添加智能配置
	@Test
	public void test116(){
		String url=host+"incomeLoss/insertIncomeLoss.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", "42557");
		map.put("income", "0.1");
		map.put("loss", "0.1");
		map.put("token", "E36E4E7F720140F1BA4E6D952ED8E4142017071914552436925CA9");
		
		System.out.println(doPost(url, map));
	}
	
	//删除智能配置
	@Test
	public void test117(){
		String url=host+"incomeLoss/deleteIncomeLoss.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", "81");
		map.put("token", "D24F2846FF454E50B92B47598FFCF3072017090815294936925CA9");
		System.out.println(doPost(url, map));
	}
	
//	@Test
//	public void test1116(){
//		String url=host+"incomeLoss/revenue.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("Ef", "25%");
//		map.put("Ed", "25%");
//		map.put("Es", "25%");
//		map.put("Ec", "25%");
//		System.out.println(doPost(url, map));
//	}
	
	//获取智能配置总资产
	@Test
	public void test1117(){
		String url=host+"incomeLoss/totalassets.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid","125");
		map.put("token", "9B18F0855A924550AE31103A758ACC7E2017111509030436925CA9");
		System.out.println(doPost(url, map));
	}

	@Test
	public void test118(){
		String url=host+"incomeLoss/getIncomeLoss.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", "125");
		map.put("token", "9B18F0855A924550AE31103A758ACC7E2017111509030436925CA9");
		System.out.println(doPost(url, map));
	}
	@Test
	public void test001(){
		String url=host+"incomeLoss/tt.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", "81");
		System.out.println(doPost(url, map));
	}
//	@Test
//	public void test119(){
//		String url=host+"incomeLoss/revenue.do";
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("userid", "42536");
//		map.put("Ef", "20%");
//		map.put("Ed", "20%");
//		map.put("Es", "3%");
//		map.put("Ec", "0%");
//		System.out.println(doPost(url, map));
//	}
	
	
	
	
	
	/**
	 * 人机大战、组合
	 */
	
	
	
	//登录
		@Test
		public void test120(){
			String url = host+"user2/login2.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("loginName", "15220138713");
			map.put("password", "zzx1234567");
			//本地
			//map.put("password", "xxm123456");
			System.out.println(doPost(url, map));
			}
		
		
	//获取用户组合
	@Test
	public void test121(){
		String url=host+"zuhe/queryzuhe.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", "81");
		map.put("token", "9FC600C474C94CA19A6E28DCC64A169A2017072511185936925CA9");
		System.out.println(doPost(url, map));
	}
	
	//
	@Test
	public void test122(){
		String url=host+"HSToken/getHSToken.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("openId", "864502028041440");
		System.out.println(doPost(url, map));
	}

	
	
	
	//查询报单

	@Test
	public void test123(){
		String url=host+"zuhe/queryorder.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gid", "5");
		map.put("userid", "81");
		map.put("token", "ABE7587CA88547DA8BA7C14D7DA5C6172017072717413036925CA9");
		System.out.println(doPost(url, map));
	}
	
	//下单
	
	@Test
	public void test125(){
		String url=host+"zuhe/position.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gid", "5");
		map.put("userid", "81");
		map.put("token", "AB6648CBC3EF41CCA22EB6D2C1FCC3022017072616555836925CA9");
		System.out.println(doPost(url, map));
	}
	@Test
	public void test124(){
		String url=host+"zuhe/insertorder.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gid", "5");
		map.put("userid", "81");
		map.put("token", "05EA93D029E94CCE93FD9CB8E45067912017081409110036925CA9");
		map.put("stock", "000005");
		map.put("price", "5.00");
		map.put("vol", "5");
		map.put("act", "1");
		System.out.println(doPost(url, map));
	}
	
	//交易记录
	
	@Test
	public void test126(){
		String url=host+"zuhe/records.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gid", "1");
		map.put("userid", "82");
		map.put("token", "640B7A2EF94C4D3384740C634B8B09192017072621512236925CA9");
	//	map.put("pageNow", "1");
		System.out.println(doPost(url, map));
	}
	
	@Test
	public void test127(){
		String url=host+"zuhe/queryscore.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gid", "17");
		map.put("userid", "94");
		map.put("token", "AD821EA4F85C494EA8AEBCBF269190E82017091117095736925CA9");
		System.out.println(doPost(url, map));
	}
	
	//{"deal":{"into_rate":null,"run_time":0,"deal_type":null,"repay_time":null,"new_rate":0,"min_loan_money":null,"manage_m":"收益的%","open_line":null,"lead_money":null,"start_time":null,"enddate":null,"rate":null,"transactions_type":"股票","name":null,"running_time":null,"id":null,"all_load_amount":0,"load_money":null},"running_time":17379,"load_count":0,"remain_time":-17379}dealhfghgf

	//{"deal":{"into_rate":null,"run_time":0,"deal_type":null,"repay_time":null,"new_rate":0,"min_loan_money":null,"manage_m":"收益的%","open_line":null,"lead_money":null,"start_time":null,"enddate":null,"rate":null,"transactions_type":"股票","name":null,"running_time":null,"id":null,"all_load_amount":0,"load_money":null},"running_time":17379,"load_count":0,"remain_time":-17379}datagfdgfd
	//{"into_rate":null,"run_time":0,"deal_type":null,"repay_time":null,"new_rate":0,"min_loan_money":null,"manage_m":"收益的%","open_line":null,"lead_money":null,"start_time":null,"enddate":null,"rate":null,"transactions_type":"股票","name":null,"running_time":null,"id":null,"all_load_amount":0,"load_money":null}dealhfghgf

	//策略推荐
	@Test
	public void test128(){
		String url=host+"incomeLoss/strategies.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("deal_id", "1325");
		System.out.println(doPost(url, map));
	}
	
	
	@Test
	public void test129(){
		String url=host+"TextAnalyser/analyse.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("s", "沙河股份");
		map.put("token", "0CD6AD0EF29E4A1E910F7CF79A84A5CB2017091317130736925CA9");
		System.out.println(doPost(url, map));
	}
	@Test
	public void test130(){
		String url=host+"incomeLoss/testIOS.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userid", "94");
		System.out.println(doPost(url, map));
	}
	@Test
	public void test131(){
		String url=host+"incomeLoss/testAn2.do";
		HashMap<String, String> map = new HashMap<String, String>();
		//map.put("userid", "82");
		System.out.println(doPost(url, map));
	}
	
	
	
	@Test
	public void test132(){
		String url=host+"homeinfo/bannerinfo.do";
		HashMap<String, String> map = new HashMap<String, String>();
		
		System.out.println(doPost(url, map));
	}
	@Test
	public void test133(){
		String url=host+"banner/getBanner.do";
		HashMap<String, String> map = new HashMap<String, String>();
		
		System.out.println(doPost(url, map));
	}
	
	//添加基金
	@Test
	public void test134(){
		String url=host+"fund/insertFund.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("fid", "12");
		map.put("phone", "15220138714");
		map.put("fname", "宜发基金");
		System.out.println(doPost(url, map));
	}
	
	
	
	//查询基金
	@Test
	public void test136(){
		String url=host+"fund/getFund.do";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("fid", "2");
		
		System.out.println(doPost(url, map));
	}
	
	
	//推荐基金
		@Test
		public void test137(){
			String url=host+"fund/defaultFund.do";
			HashMap<String, String> map = new HashMap<String, String>();
			
			System.out.println(doPost(url, map));
		}
		
		//设置推荐基金
		@Test
		public void test138(){
			String url=host+"fund/setDefaultFund.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fid", "2");
			System.out.println(doPost(url, map));
		}
		
		
		
		//查询各个标的比例
		@Test
		public void test139(){
			String url=host+"fund/scale.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fid", "1");
			System.out.println(doPost(url, map));
		}
		
		//添加基金总资产
		@Test
		public void test140(){
			String url=host+"fund/insertFundassets.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fid", "1");
			map.put("assets", "500000");
			System.out.println(doPost(url, map));
		}
		
		//查询基金总资产
		@Test
		public void test141(){
			String url=host+"fund/getFundassets.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fid", "1");
			System.out.println(doPost(url, map));
		}
		
		//基金推送消息
		@Test
		public void test142(){
			String url=host+"fund/fundrate.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fid", "1");
			System.out.println(doPost(url, map));
		}
		//自选股推荐
		@Test
		public void test143(){
			String url=host+"incomeLoss/bspoint.do";
			HashMap<String, String> map = new HashMap<String, String>();
			System.out.println(doPost(url, map));
		}
		
		//自选股单人推送
		@Test
		public void test1444(){
			String url=host+"incomeLoss/bspoint2.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("userid", "125");
			System.out.println(doPost(url, map));
		}
		
		
		//策略推荐
		@Test
		public void test144(){
			String url=host+"incomeLoss/strategies.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("deal_id", "1325");
			System.out.println(doPost(url, map));
		}
		
		
		//强制更新token
		@Test
		public void test145(){
			String url=host+"HSToken/getTokenLoginOnWhenException.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("userId", "42557");
			System.out.println(doPost(url, map));
		}
		
		//已读
		@Test
		public void test146(){
			String url=host+"send/stockOrYhzhReaded.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", "1652");
			map.put("type", "6");
			System.out.println(doPost(url, map));
		}
		
		//自选股未读
		@Test
		public void test147(){
			String url=host+"send/stockunread.do";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("userid", "1854");
			System.out.println(doPost(url, map));
		}
	//	{"msg":"操作成功","data":[{"id":1394,"userid":42557,"stockid":"000001","stockcode":"000001.SZ","name":"平安银行","bspoint":1,"time":"2017/08/11 18:11:22","type":"1","status":0}],"status":0}
	//	{"msg":"操作成功","data":[{"id":1394,"userid":42557,"stockid":"000001","stockcode":"000001.SZ","name":"平安银行","bspoint":1,"time":"2017/08/11 18:11:22","type":"1","status":0},{"id":1452,"userid":42557,"stockid":"000002","stockcode":"000002.SZ","name":"万  科Ａ","bspoint":1,"time":"2017/08/11 18:11:22","type":"1","status":0}],"status":0}
		
		//优化组合未读
				@Test
				public void test148(){
					String url=host+"send/yhzhunread.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "81");
					System.out.println(doPost(url, map));
				}
				
				@Test
				public void test149(){
					String url=host+"zuhe/analysis.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("main_rate", "-0.012248");
					map.put("optimize_rate", "0.000000");
					map.put("market_rate", "-0.001725");
					
					System.out.println(doPost(url, map));
				}
				
				@Test
				public void test150(){
					String url=host+"send/stockandyhzhunread.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "81");
					System.out.println(doPost(url, map));
				}
				
				//每天下午输入revenue
				@Test
				public void test151(){
					String url=host+"incomeLoss/revenue.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("Ef", "0.00174");//期货
					map.put("Ed", "0.00035");//债券
					map.put("Es", "-0.0003");//股票
					map.put("Ec", "0");//现金
					map.put("sharesmoney", "3998116");
					map.put("futuresmoney", "6071713");
					System.out.println(doPost(url, map));
				}
				
				
				
				//获取股票持仓
				@Test
				public void test154(){
					String url=host+"FuturesShares/getSharesPositionByGT.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "125");
					map.put("p", "2");
					map.put("count", "5");
					System.out.println(doPost(url, map));
				}
				
					
				
				//查询所有期货持仓
				@Test
				public void test155(){
					String url=host+"FuturesTraderec/getFuturesPositionByGT.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "48704");
					map.put("p", "1");
					map.put("count", "5");
					System.out.println(doPost(url, map));
				}
				
				//查询股票交易记录
				@Test
				public void test156(){
					String url=host+"FuturesShares/getSharesRecord.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "125");
					//System.out.println("gdfhnklgfdkj");
//					map.put("p", "1");
//					map.put("count", "5");
					System.out.println(doPost(url, map));
				}
				//查询所有期货交易记录
				@Test
				public void test157(){
					String url=host+"FuturesTraderec/getFuturesRecord.do";
					HashMap<String, String> map = new HashMap<String, String>();
					//System.out.println("gdfhnklgfdkj");
					map.put("userid", "125");
					map.put("p", "1");
					//map.put("count", "3");
					System.out.println(doPost(url, map));
				}
				
				@Test
				public void test1500(){
					String url=host+"incomeLoss/insertIncomeLossByGT.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "125");
					map.put("income", "0.2");
					map.put("loss", "0.2");
					System.out.println(doPost(url, map));
				}	
				
				@Test
				public void test1501(){
					String url=host+"incomeLoss/getIncomeLossByGT.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "125");
					//map.put("income", "0.3");
					//map.put("loss", "0.2");
					System.out.println(doPost(url, map));
				}
				
				@Test
				public void test1502(){
					String url=host+"incomeLoss/totalassetsByGT.do";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("userid", "125");
					//map.put("income", "0.3");
					//map.put("loss", "0.2");
					System.out.println(doPost(url, map));
				}	

}
