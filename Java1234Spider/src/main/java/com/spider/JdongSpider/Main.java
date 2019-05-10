package com.spider.JdongSpider;
 
import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
 
/**
 * 程序入口，在此声明客户端，并向服务器发送请求
 * @author 康茜
 *
 */
@SuppressWarnings("deprecation")
public class Main {
	public static void main(String[] args) {
		//生成一个客户端，通过客户端可url向服务器发送请求，并接收响应
		HttpClient client = new DefaultHttpClient();
		String url = "http://www.java1234.com/a/javabook/javabase/";
		try {
			 URLHandle.urlParser(client, url);
			
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	
	}
}