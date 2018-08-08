package com.kettle.debug;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.engine.FastRuntimeEngine;
import org.beetl.core.misc.BeetlUtil;
import org.beetl.core.resource.AllowAllMatcher;
import org.beetl.core.resource.CompositeResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;

import com.ram.beetl.FileUtilsFunction;
public class BettlDebug extends TestCase {
	GroupTemplate gt;
	GroupTemplate gtFile;

	public void setUp() {
		try {
			StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
			Configuration cfg = Configuration.defaultConfiguration();
			cfg.setDirectByteOutput(false);// false : 1615 true:2475 render()情况下
											// false速度较快， true的情况下 是 数据流情况下 效率高 
			gt = new GroupTemplate(resourceLoader, cfg);

			org.beetl.core.resource.FileResourceLoader fileResourceLoader = new FileResourceLoader();
			Configuration fcfg = Configuration.defaultConfiguration();
			fcfg.setDirectByteOutput(true);
			gtFile = new GroupTemplate(fileResourceLoader, fcfg);

			CompositeResourceLoader loader = new CompositeResourceLoader();
			loader.addResourceLoader(new AllowAllMatcher(), fileResourceLoader);
			loader.addResourceLoader(new AllowAllMatcher(), resourceLoader);

			GroupTemplate gt = new GroupTemplate(loader, cfg);
			FastRuntimeEngine x=null;
		} catch (IOException e) {
		}
	}

	class XBean {
		public String z="1";
		public String getF(String z) {
			System.out.println("=XBean getF=");
			return z;
		}
		public String getZ() {
			System.out.println("=XBean t=");
			return "1";
		}
	}

	public void testBeetl() throws Exception {
		StringBuffer temp = new StringBuffer("");

		temp.append("<% var jsonstr=\"[{\\\"control_type\\\":\\\"div\\\",\\\"control_label\\\":\\\"<div>\\\",\\\"control_field\\\":\\\"<div>\\\",\\\"control_layout\\\":\\\"layui-col-xs12\\\"}]\"; println(jsonstr); var json=[{}];	\r\n	var outHtml=\"\"; 	\r\n	for(var element in json){ 		\r\n	outHtml=outHtml+element+\"\";		\r\n	} \r\n debug(outHtml); %>");
		XBean bean=new XBean();
		
		Template t = gt.getTemplate(temp.toString());
		t.binding("fff",bean);
		
		t.binding("name", "beetl");
		// java.io.OutputStream os=new FileOutputStream("D:/1.txt");
		// t.renderTo(os); //用 cfg.setDirectByteOutput(true);
		String str = t.render();
		System.out.println(" str:" + str);
	}
	
	public void testX() throws Exception{
		FileUtilsFunction fileUtils = new FileUtilsFunction();
		String filepath = "view/index.html";
		String fileContent = "" + fileUtils.getFileContent(filepath, "");
		// System.out.println("filecontent:" + fileContent);

		String webRoot = BeetlUtil.getWebRoot();

		// System.out.println("webRoot:" + webRoot);
		String key = "FX";
		// fileUtils.saveFileContent(filepath+".bak", "", fileContent);
		fileUtils.putCache(key, "zz", 5);
		// Thread.sleep(2000);
		Object cacheData = fileUtils.getCache(key);
		System.out.println("cacheData:" + cacheData);

		Date startTime = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.add(Calendar.SECOND, 5);
		Date now = new Date();
		long between = cal.getTimeInMillis() - now.getTime();
		System.out.println("cal.getTimeInMillis():" + cal.getTimeInMillis());
		System.out.println("now.getTimeInMillis():" + now.getTime());
	}

	public void testCache() throws Exception {
		FileUtilsFunction fileUtils = new FileUtilsFunction();
		for (int i = 0; i < 1000000; i++) {
			fileUtils.putCache(i + "", i + "ff");
		}
		System.out.println("==testCache=" + fileUtils.getCache(10000 + ""));
		Thread.sleep(10000);
	}

	public void testWebRoot() throws URISyntaxException, IOException {

		String path = BeetlUtil.class.getClassLoader().getResource("").toURI()
				.getPath();
		if (path == null) {
			throw new NullPointerException("Beetl未能自动检测到WebRoot，请手工指定WebRoot路径");
		}
		String root = new File(path).getParentFile().getParentFile()
				.getCanonicalPath();
		System.out.println("path:" + path);
		System.out.println("root:" + root);

	}

}