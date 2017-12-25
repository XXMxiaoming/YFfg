package com.yfwl.yfgp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;

import com.yfwl.yfgp.model.StockXml;

public class StockUpDownUtils {

	/*public static void main(String[] args) {
		Document document = getDocument();
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> childElements = root.elements();
		for(Element child : childElements){
			System.out.println("Zqjc:" + child.elementText("Zqjc"));
		}
		
	}*/

	public static List<StockXml> analyzeUpDownXml() {

		List<StockXml> list = new ArrayList<StockXml>();
		Document document = getDocument();
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> childElements = root.elements();
		for(Element child : childElements){
			
			String zqjc = child.elementText("Zqjc");
			String zqdm = child.elementText("Zqdm");
			String bS_Day_Date = child.elementText("BS_Day_Date");
			String day_Cjj = child.elementText("Day_Cjj");
			String up_Down = child.elementText("Up_Down");
			String info = child.elementText("Info");
			
			StockXml stockXml = new StockXml();
			stockXml.setZqjc(zqjc);
			stockXml.setZqdm(zqdm);
			stockXml.setBS_Day_Date(bS_Day_Date);
			stockXml.setDay_Cjj(day_Cjj);
			if(up_Down.equals("1")){
				stockXml.setUp_Down("上升");
				stockXml.setOperate("加仓");
			}else{
				stockXml.setUp_Down("下跌");
				stockXml.setOperate("减仓");
			}
			stockXml.setInfo(info);
			list.add(stockXml);
		}
		
		return list;
	}

	// 获取URL页面XML的Document
	private static Document getDocument() {
		try {
			URL url = new URL("http://112.74.87.151/allqszs.xml");
			try {
				URLConnection URLconnection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream in = httpConnection.getInputStream();
					try {
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = factory.newDocumentBuilder();
						org.w3c.dom.Document doc = db.parse(in);
						DOMReader domReader = new DOMReader();
						Document document = domReader.read((org.w3c.dom.Document) doc);
						return document;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("HTTP connection response !=HTTP_OK");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
