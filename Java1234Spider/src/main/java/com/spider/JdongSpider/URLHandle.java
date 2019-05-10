
package com.spider.JdongSpider;
 
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
 
/**
 * 通过URL和客户端（client）处理请求返回的数据
 * @author 康茜
 *
 */
public class URLHandle {
	/**
	 * 
	 * @param client 客户端
	 * @param url 请求地址
	 * @return 请求数据 ：List<JdongBook>
	 * @throws ParseException
	 * @throws IOException
	 */
	public static PageResult<Java1234Book> urlParser(HttpClient client, String url) throws ParseException, IOException {
		PageResult<Java1234Book> pageResult=null;
		
		//获取响应资源
		HttpResponse response = HTTPUtils.getHtml(client, url);
		//获取响应的状态码
		int sattusCode = response.getStatusLine().getStatusCode();
		if(sattusCode == 200) {//200表示成功
			//获取响应实体内容，并且将其转换为utf-8形式的字符串编码
			String entity = EntityUtils.toString(response.getEntity(), "utf-8");
			pageResult = Java1234Parse.getData(client,entity);
		} else {
			EntityUtils.consume(response.getEntity());//释放资源实体
		}
		return pageResult;
	}
}