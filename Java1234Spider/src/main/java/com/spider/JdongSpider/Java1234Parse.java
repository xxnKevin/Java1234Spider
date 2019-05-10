package com.spider.JdongSpider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;

public class Java1234Parse {
	public static ESData esData=new ESData();
	public static String docPath="E:\\爬虫java1234\\";
	/**
	 * 根据实体获取程序所需数据
	 * 
	 * @param entity HTTP响应实体内容
	 * @return
	 */
	public static PageResult<Java1234Book> getData(HttpClient client,String entity) {
		System.out.println(">>>>>>>>>>>>>>抓取开始,结果会存储到你电脑"+docPath+"，图书很多，请耐心等待，祝好运！！！<<<<<<<<<<<<<<<<<");
		long start=System.currentTimeMillis();
		File docFile=new File(docPath);
		if(!docFile.exists()) {
			docFile.mkdir();
		}
		List<Java1234Book> data = new ArrayList<>();
		PageResult<Java1234Book> pageResult = new PageResult<>(0, data);
		// 采用jsoup解析，关于jsoup的使用，见下文总结
		Document doc = Jsoup.parse(entity);
		Elements elements = doc.select(".e2").select("li");
		Elements elementsPage = doc.select(".pagelist").select("li");
		pageResult.setTotal(Integer.parseInt(elementsPage.select(".pageinfo").select("strong").last().text()));
		int totalPages = Integer.parseInt(elementsPage.select(".pageinfo").select("strong").first().text());
		int total = Integer.parseInt(elementsPage.select(".pageinfo").select("strong").last().text());
		System.out.println("==============总共"+totalPages+"页,"+total+"本书===========");
		totalPages=total;
		for (Element element : elements) {
			Java1234Book book = new Java1234Book();
			book.setBookDate(element.select(".info").text().substring(3));
			String name = element.select(".title").text();
			book.setBookTag(book.praseTag(name));
			book.setBookName(name.replace("下载", "").replace("PDF", ""));
			book.setBookPicture("http://www.java1234.com/" + element.select(".preview").select("img").attr("src"));
			book.setDownLoadAddress("http://www.java1234.com"+element.select(".title").attr("href"));
			data.add(book);
			handleFile(book);
		}
		for (int i = 2; i <= totalPages; i++) {
			startChidPages(client,pageResult, i);
		}
		System.out.println(">>>>>>>>>>>>>>抓取完毕<<<<<<<<<<<<<<<<<");
		System.out.println(">>>>>>>>>>>>>>用时:"+(System.currentTimeMillis()-start)/100.0+"秒<<<<<<<<<<<<<<<<<");
		FileUtils.writeESTxt(docPath);
		return pageResult;
	}

	public static void handleFile(Java1234Book book) {
		try {
			FileUtils.writeBookTxt(docPath, book);
			FileUtils.downloadPic(book, docPath);
			Thread.sleep(500);
			System.out.println(">>>>>>>>>>>>>>>>>>爬虫图书： 【" + book.getBookName() + "】成功<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startChidPages(HttpClient client,PageResult<Java1234Book> pageResult, int index) {
		try {
			// 获取响应资源
			HttpResponse response = HTTPUtils.getHtml(client, "http://www.java1234.com/a/javabook/javabase/list_65_"+index+".html");
			// 获取响应的状态码
			int sattusCode = response.getStatusLine().getStatusCode();
			if (sattusCode == 200) {// 200表示成功
				// 获取响应实体内容，并且将其转换为utf-8形式的字符串编码
				String entity = EntityUtils.toString(response.getEntity(), "utf-8");
				List<Java1234Book> data = new ArrayList<>();
				// 采用jsoup解析，关于jsoup的使用，见下文总结
				Document doc = Jsoup.parse(entity);
				Elements elements = doc.select(".e2").select("li");

				for (Element element : elements) {
					Java1234Book book = new Java1234Book();
					book.setBookDate(element.select(".info").text().substring(3));
					String name = element.select(".title").text();
					book.setBookTag(book.praseTag(name));
					book.setBookName(name.replace("下载", "").replace("PDF", ""));
					book.setBookPicture("http://www.java1234.com/" + element.select(".preview").select("img").attr("src"));
					book.setDownLoadAddress("http://www.java1234.com"+element.select(".title").attr("href"));
					data.add(book);
					handleFile(book);
				}
				pageResult.getData().addAll(data);
			} else {
				EntityUtils.consume(response.getEntity());// 释放资源实体
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}