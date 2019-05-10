package com.spider.JdongSpider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString 
public class Java1234Book {
	private String bookDate;
	private String bookName;
	private String bookPicture;
	private String detailInfo;
	private String downLoadUrl;
	private String downLoadPwd;
	private BookTag bookTag;
	
	public BookTag praseTag(String bookName) {
		Java1234Parse.esData.setTotal(Java1234Parse.esData.getTotal()+1);
		if(bookName.contains("java")||bookName.contains("Java")||bookName.contains("JAVA")) {
			Java1234Parse.esData.setJavaTotal(Java1234Parse.esData.getJavaTotal()+1);
			return BookTag.JAVA;
		}if(bookName.contains("python")||bookName.contains("Python")||bookName.contains("PYTHON")) {
			Java1234Parse.esData.setPythonTotal(Java1234Parse.esData.getPythonTotal()+1);
			return BookTag.PYTHON;
		}if(bookName.equalsIgnoreCase("web")||bookName.contains("Web")||bookName.contains("WEB")) {
			Java1234Parse.esData.setWebTotal(Java1234Parse.esData.getWebTotal()+1);
			return BookTag.WEB;
		}
		Java1234Parse.esData.setOtherTotal(Java1234Parse.esData.getOtherTotal()+1);
		return BookTag.OTHER;
	}
}