package com.spider.JdongSpider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class FileUtils {

	/**
	 * 写入指定路径文本文档
	 *
	 * @param filePath
	 */
	public static void writeBookTxt(String filePath,Java1234Book book) {

		File file = new File(filePath+"\\"+book.getBookName()+".txt");
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			String data;

			data = "书名："+book.getBookName();
			// 写入文本文档
			writer.write(data);
			// 换行
			writer.write("\r\n");
			
			/*data = "图片地址："+book.getBookPicture();
			writer.write(data);
			writer.write("\r\n");*/
			
			data = "内容简介："+book.getDetailInfo();
			writer.write(data);
			writer.write("\r\n");
			
			data = "上架时间："+book.getBookDate();
			writer.write(data);
			writer.write("\r\n");

			data = "下载网盘地址："+book.getDownLoadUrl();
			writer.write(data);
			writer.write("\r\n");
			
			data = "密码："+book.getDownLoadPwd();
			writer.write(data);
			writer.write("\r\n");
			
			

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 写入指定路径文本文档
	 *
	 * @param filePath
	 */
	public static void writeESTxt(String filePath) {

		File file = new File(filePath+"\\爬虫结果分析.txt");
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			StringBuffer data=new StringBuffer();
			ESData esData=Java1234Parse.esData;
			data.append("一共爬到书："+esData.getTotal()+"本");

			// 换行
			data.append("\r\n");
			data.append("\r\n");
			
			data.append(" java类图书："+esData.getJavaTotal()+"本");
			data.append("\r\n");
			
			data.append(" python类图书："+esData.getPythonTotal()+"本");
			data.append("\r\n");

			data.append(" web类图书："+esData.getWebTotal()+"本");
			data.append("\r\n");
			
			data.append("  未分类图书："+esData.getOtherTotal()+"本");
			data.append("\r\n");
			
			data.append("爬虫时间："+new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date()));
			data.append("\r\n");
			data.append("作者："+"https://github.com/blue19demon");
			// 写入文本文档
			writer.write(data.toString());
            System.out.println(data);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * 从网络上下载图片
	 */
	public static void downloadPicture(String url, String dirPath, String filePath) {

		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet(url);

		httpget.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
		httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

		try {
			HttpResponse resp = httpclient.execute(httpget);
			if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
				HttpEntity entity = resp.getEntity();

				InputStream in = entity.getContent();

				savePicToDisk(in, dirPath, filePath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 将图片写到 硬盘指定目录下
	 * 
	 * @param in
	 * @param dirPath
	 * @param filePath
	 */
	private static void savePicToDisk(InputStream in, String dirPath, String filePath) {

		try {
			
			// 文件真实路径
			String realPath = dirPath.concat(filePath);
			File file = new File(realPath);
			if (file == null || !file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			fos.flush();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void downloadPic(Java1234Book book, String bookDir) {
		String bookPicture=book.getBookPicture();
		String realExt=bookPicture.substring(bookPicture.lastIndexOf("/")+1,bookPicture.length());
		realExt=realExt.substring(realExt.lastIndexOf("."),realExt.length());
		downloadPicture(bookPicture, bookDir, book.getBookName()+realExt);
	}

}