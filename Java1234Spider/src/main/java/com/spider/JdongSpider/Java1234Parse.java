package com.spider.JdongSpider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Java1234Parse {
	public static ESData esData = new ESData();
	public static String docPath = "E:\\爬虫电子书\\";
	public static AtomicInteger atIndex = new AtomicInteger(1);

	/**
	 * 根据实体获取程序所需数据
	 * 
	 * @param entity HTTP响应实体内容
	 * @return
	 */
	public static PageResult<Java1234Book> getData(HttpClient client, String entity) {
		try {
			System.out.println(">>>>>>>>>>>>>>抓取开始,结果会存储到你电脑" + docPath + "，图书很多，请耐心等待，祝好运！！！<<<<<<<<<<<<<<<<<");
			long start = System.currentTimeMillis();
			File docFile = new File(docPath);
			if (!docFile.exists()) {
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
			System.out.println("==============总共" + totalPages + "页," + total + "本书===========");
			totalPages = total;
			for (Element element : elements) {
				System.out.println("开始下载第" + atIndex.getAndIncrement() + "本书....");
				Java1234Book book = new Java1234Book();
				book.setBookDate(element.select(".info").text().substring(3));
				String name = element.select(".title").text().trim();
				book.setBookTag(book.praseTag(name));
				book.setBookName(name.replace("下载", "").replace("PDF", "").trim());
				book.setBookPicture("http://www.java1234.com/" + element.select(".preview").select("img").attr("src"));
				String detailURL = "http://www.java1234.com" + element.select(".title").attr("href");
				HttpResponse response = HTTPUtils.getHtml(client, detailURL);
				int sattusCode = response.getStatusLine().getStatusCode();
				if (sattusCode == 200) {// 200表示成功
					entity = EntityUtils.toString(response.getEntity(), "utf-8");
					doc = Jsoup.parse(entity);
					Element ele = doc.select(".content").select("table").select("tbody").select("tr").last()
							.select("td").first();
					Elements es = ele.select("span").select("strong");
					String downLoadUrl = "";
					if (es.size() >= 1) {
						downLoadUrl += es.get(0).select("a").attr("href");
					}
					if (es.size() >= 2) {
						downLoadUrl += es.get(1).select("a").attr("href");
					}
					if (es.size() >= 3) {
						downLoadUrl += es.get(2).select("a").attr("href");
					}
					if (es.size() >= 4) {
						downLoadUrl += es.get(3).select("a").attr("href");
					}
					String downLoadPwd = "";
					if (es.size() >= 1) {
						downLoadPwd += es.get(0).select("span").text();
					}
					if (es.size() >= 2) {
						downLoadPwd += es.get(1).select("span").text();
					}
					if (es.size() >= 3) {
						downLoadPwd += es.get(2).select("span").text();
					}
					if (es.size() >= 4) {
						downLoadPwd += es.get(3).select("span").text();
					}
					if (es.size() >= 5) {
						downLoadPwd += es.get(4).select("span").text();
					}
					// System.out.println(downLoadUrl);
					// System.out.println(downLoadPwd);
					/// html/body/div[6]/div[1]/div[2]/div[5]/table/tbody/tr[2]/td/div[5]/span[2]
					es = ele.select("div").select("span");
					StringBuffer sb = new StringBuffer("");
					if (es.size() >= 6) {
						// System.out.println("+++++++"+es.get(5).siblingElements().html());// 资料简介内容
						sb.append(es.get(5).siblingElements().html());
					}
					if (es.size() >= 15) {
						// System.out.println("----------"+es.get(14).html());// 资料简介内容
						sb.append(es.get(14).html());
					}
					book.setDownLoadUrl(downLoadUrl);
					book.setDownLoadPwd(downLoadPwd);
					book.setDetailInfo(sb.toString());
				} else {
					EntityUtils.consume(response.getEntity());// 释放资源实体
				}
				data.add(book);
				handleFile(book);
			}
			for (int i = 2; i <= totalPages; i++) {//totalPages
				startChidPages(client, pageResult, i);
			}

			System.out.println(">>>>>>>>>>>>>>抓取完毕<<<<<<<<<<<<<<<<<");
			System.out.println(">>>>>>>>>>>>>>用时:"
					+ getTime(String.valueOf((System.currentTimeMillis() - start) / 1000)) + "<<<<<<<<<<<<<<<<<");
			FileUtils.writeESTxt(docPath);
			return pageResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void handleFile(Java1234Book book) {
		try {
			FileUtils.writeBookTxt(docPath, book);
			FileUtils.downloadPic(book, docPath);
			Thread.sleep(500);
			System.out.println("【" + book.getBookName() + "】下载成功!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startChidPages(HttpClient client, PageResult<Java1234Book> pageResult, int index) {
		try {
			// 获取响应资源
			HttpResponse response = HTTPUtils.getHtml(client,
					"http://www.java1234.com/a/javabook/javabase/list_65_" + index + ".html");
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
					System.out.println("开始下载第" + atIndex.getAndIncrement() + "本书....");
					Java1234Book book = new Java1234Book();
					book.setBookDate(element.select(".info").text().substring(3));
					String name = element.select(".title").text().trim();
					book.setBookTag(book.praseTag(name));
					book.setBookName(name.replace("下载", "").replace("PDF", "").trim());
					book.setBookPicture(
							"http://www.java1234.com/" + element.select(".preview").select("img").attr("src"));
					String detailURL = "http://www.java1234.com" + element.select(".title").attr("href");
					response = HTTPUtils.getHtml(client, detailURL);
					if (sattusCode == 200) {// 200表示成功
						entity = EntityUtils.toString(response.getEntity(), "utf-8");
						doc = Jsoup.parse(entity);
						Element ele = doc.select(".content").select("table").select("tbody").select("tr").last()
								.select("td").first();
						Elements es = ele.select("span").select("strong");
						String downLoadUrl = "";
						if (es.size() >= 1) {
							downLoadUrl += es.get(0).select("a").attr("href");
						}
						if (es.size() >= 2) {
							downLoadUrl += es.get(1).select("a").attr("href");
						}
						if (es.size() >= 3) {
							downLoadUrl += es.get(2).select("a").attr("href");
						}
						if (es.size() >= 4) {
							downLoadUrl += es.get(3).select("a").attr("href");
						}
						String downLoadPwd = "";
						if (es.size() >= 1) {
							downLoadPwd += es.get(0).select("span").text();
						}
						if (es.size() >= 2) {
							downLoadPwd += es.get(1).select("span").text();
						}
						if (es.size() >= 3) {
							downLoadPwd += es.get(2).select("span").text();
						}
						if (es.size() >= 4) {
							downLoadPwd += es.get(3).select("span").text();
						}
						if (es.size() >= 5) {
							downLoadPwd += es.get(4).select("span").text();
						}
						es = ele.select("div").select("span");
						StringBuffer sb = new StringBuffer("");
						if (es.size() >= 6) {
							// System.out.println("+++++++"+es.get(5).siblingElements().html());// 资料简介内容
							sb.append(es.get(5).siblingElements().html());
						}
						if (es.size() >= 15) {
							// System.out.println("----------"+es.get(14).html());// 资料简介内容
							sb.append(es.get(14).html());
						}
						book.setDownLoadUrl(downLoadUrl);
						book.setDownLoadPwd(downLoadPwd);
						book.setDetailInfo(sb.toString());
					} else {
						EntityUtils.consume(response.getEntity());// 释放资源实体
					}
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

	private static String getTime(String str) {
		int seconds = Integer.parseInt(str);
		int temp = 0;
		StringBuffer sb = new StringBuffer();
		temp = seconds / 3600;
		sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

		temp = seconds % 3600 / 60;
		sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

		temp = seconds % 3600 % 60;
		sb.append((temp < 10) ? "0" + temp : "" + temp);
		return sb.toString();
	}
}