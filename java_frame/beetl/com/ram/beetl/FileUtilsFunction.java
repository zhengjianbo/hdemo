package com.ram.beetl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.beetl.core.misc.BeetlUtil;

import com.ram.kettle.database.CacheApplication;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.kettle.util.FileUtils;
import com.ram.kettle.xml.KettleVFS;
import com.ram.server.util.BaseLog;

public class FileUtilsFunction {
 
	private BeetlApplication cacheApp = BeetlApplication.getInstanceSingle();
	private CacheApplication dimApp = CacheApplication.getInstanceSingle();

	public Object getWebRoot() {
		return BeetlUtil.getWebRoot();
	}

	public Object setVariable(Object key, Object entry) throws Exception {
		dimApp.put(key + "", entry);
		return true;
	}

	public Object getVariable(Object key) throws Exception {
		return dimApp.get(key + "");
	}

	public Object putCache(Object key, Object entry, int timeout)
			throws Exception {
		cacheApp.put(key + "", entry, timeout);
		return true;
	}

	// 获取cookie列表 ,然后循环进行判断
	public Object listCache() {
		return cacheApp.getDimCacheMap();
	} 

	public Object putCache(Object key, Object entry) throws Exception {
		cacheApp.put(key + "", entry);
		return true;
	}

	public Object getCache(Object key) throws Exception {
		return cacheApp.get(key + "");
	}
	public void remove(Object key) throws Exception {
		  cacheApp.remove(key + "");
	}

	public boolean isTimeOutCheck(Object key) throws Exception {
		return cacheApp.isTimeOutCheck(key + "");
	}
	public boolean isCacheTimeOut(Object key) throws Exception {
		return cacheApp.isTimeOut(key + "");
	}

	public String[][] getFolderWebList(String filePath) throws Exception {
		return getFolderWebList(filePath, true);
	}
	public String[][] getFolderWebList(String filePath,
			final boolean containFiles,int page,int pagenum) throws Exception {
		filePath = BeetlUtil.getWebRoot() + filePath;
		FileObject[] fList = getFolderList(filePath, containFiles, (page-1)*pagenum+1, pagenum);

		String[][] returnList = new String[fList.length][2];
		int index = 0;
		for (FileObject f : fList) {
			returnList[index++] = new String[] {
					f.getName().getBaseName(),
					KettleVFS.getFilename(f)
							.replace(BeetlUtil.getWebRoot(), "") };
		}
		return returnList;
	}
	public String[][] getFolderWebList(String filePath,
			final boolean containFiles) throws Exception {
		 return getFolderWebList(filePath,containFiles,1,20);
	}

	// 默认一次100个文件
	public FileObject[] getFolderWebRootList(String filePath) throws Exception {
		return getFolderWebRootList(filePath, true);
	}

	public FileObject[] getFolderWebRootList(String filePath,
			final boolean containFiles) throws Exception {
		filePath = BeetlUtil.getWebRoot() + filePath;
		return getFolderList(filePath, containFiles, 0, 100);
	}

	/**
	 * 读取目录下的文件以及文件夹列表列表
	 * 
	 * @param filePath
	 *            目录
	 * @param containFiles
	 *            是否列出文件
	 * @param start
	 *            记录开始数
	 * @param num
	 *            文件数量（文件和目录）
	 * @return
	 * @throws KettleException
	 * @throws FileSystemException
	 */
	public FileObject[] getFolderList(String filePath,
			final boolean containFiles, final int start, final int num)
			throws Exception {
		if (Const.isEmpty(filePath))
			return null;

		FileObject directoryFileObject = KettleVFS.getFileObject(filePath);
		if (directoryFileObject.exists()
				&& directoryFileObject.getType() == FileType.FOLDER) {

			FileObject[] fileObjects = directoryFileObject
					.findFiles(new AllFileSelector() {
						int startIndex = 0;

						public boolean traverseDescendents(FileSelectInfo info) {
							return info.getDepth() == 0 || false;
						}

						public boolean includeFile(FileSelectInfo info) {
							if (info.getDepth() == 0) {
								return false;
							}
							FileObject fileObject = info.getFile();
							try {
								if (fileObject != null) {
									if (containFiles) {
										if (num == -1) {
											return true;
										}
										if (startIndex >= start
												&& startIndex < start + num) {
											// 在范围内才可以
											startIndex++;
											return true;
										}
										return false;
									} else {
										if (fileObject.getType() == FileType.FOLDER) {
											if (num == -1) {
												return true;
											}
											if (startIndex >= start
													&& startIndex < start + num) {
												// 在范围内才可以
												startIndex++;
												return true;
											}
											return false;
										}
									}
								}
								return false;
							} catch (FileSystemException ex) {
								return false;
							}
						}
					});

			return fileObjects;
		}
		return null;
	}

	/**
	 * 模板编辑的头部内容
	 */
	public Object getFromFileContent(Object filePath)
			throws KettleException {
		filePath = "/template" + filePath;

		String checkFilePath = BeetlUtil.getWebRoot() + filePath;

		File f = new File(checkFilePath);
		if (f.exists() && f.isFile()) {

		} else {
			return "";
		}
 
		return getFileContentX(filePath, "UTF-8");
	}
	/**
	 * 模板编辑的头部内容
	 */
	public Object getFileEditTempateHeaderContent(Object filePath)
			throws KettleException {
		filePath = "/template/header" + filePath;

		String checkFilePath = BeetlUtil.getWebRoot() + filePath;

		File f = new File(checkFilePath);
		if (f.exists() && f.isFile()) {

		} else {
			filePath = "/template/header/default.html";
		}

		return getFileContent(filePath, "UTF-8");
	}
	/**
	 * 模板编辑的尾部内容
	 */
	public Object getFileEditTempateFooterContent(Object filePath)
			throws KettleException {
		filePath = "/template/footer" + filePath;

		String checkFilePath = BeetlUtil.getWebRoot() + filePath;

		File f = new File(checkFilePath);
		if (f.exists() && f.isFile()) {

		} else {
			filePath = "/template/footer/default.html";
		}

		return getFileContent(filePath, "UTF-8");
	}

	// 读取 layit 编辑的内容 (可视化编辑内容和保存实际html内容不一致)
	public Object getFileEditTempateContent(Object filePath)
			throws KettleException {
		return getFileEditTempateContent(filePath, "UTF-8");
	}

	public Object getFileEditTempateContent(Object filePath, Object encode)
			throws KettleException {
		Object[] returnContent = new Object[2];

		// 如果目录总不存在该模板文件 则读取默认文件内容 截取头和尾 来处理
		String fTempPath = BeetlUtil.getWebRoot() + "/template" + filePath;
		if (new File(fTempPath).exists()) {
			String tempfilePath = "/template" + filePath;
			return getFileContent(tempfilePath, encode);
		} else {
			String fPath = BeetlUtil.getWebRoot() + filePath;
			if (new File(fPath).exists()) {
				// 读取内容 ，然后只读取html 中的<body>部分 然后 截取内容
				String html = FileUtils.getTextFileContent(fPath, encode + "");
				// 正则截取
				StringBuffer trs = new StringBuffer();
				try {
					Pattern tableMatchPattern = Pattern
							.compile("<body[^>]*?>([\\s\\S]*?)</body>");
					Matcher m = tableMatchPattern.matcher(html.trim());
					while (m.find()) {
						if (m.group() != null) {
							trs.append(m.group(1).trim());
						}
					}
				} catch (Exception e) {
					throw new KettleException("No FOUND TEMPLATE CONTENT");
				}

				// 加入 数据 然后 用BEETL解析 替换 然后执行
				String vhtmlPath = BeetlUtil.getWebRoot()
						+ "/template/view.html";
				String vhtml = FileUtils.getTextFileContent(vhtmlPath, encode
						+ "");
				returnContent[0] = vhtml.replace("${html}", trs.toString());

			}
			return returnContent;
		}

	}

	public Object[] getFileContent(Object filePath) throws KettleException {
		Object[] returnContent = new Object[2];
		returnContent[0] = getFileContentX(filePath, "UTF-8");
		return returnContent;
	}

	public Object[] getFileContent(Object filePath, Object encode)
			throws KettleException {
		Object[] returnContent = new Object[2];
		returnContent[0] = getFileContentX(filePath, encode);
		return returnContent;
	}

	private Object getFileContentX(Object filePath, Object encode)
			throws KettleException {
		if (filePath != null) {
			if (filePath.getClass() == "".getClass()) {

				filePath = BeetlUtil.getWebRoot() + filePath;
				if (encode == null || Const.isEmpty(encode + "")) {
					return FileUtils.getTextFileContent(filePath + "", "");
				} else {
					return FileUtils.getTextFileContent(filePath + "", encode
							+ "");
				}
			}
		}
		return null;
	}

	public Object saveFileTemplateHeaderContent(Object filePath,
			Object fileContent) throws Exception {
		filePath = "/template/header" + filePath;
		return this.saveFileContent(filePath, "UTF-8", fileContent);
	}

	public Object saveFileTemplateFooterContent(Object filePath,
			Object fileContent) throws Exception {
		filePath = "/template/footer" + filePath;
		return this.saveFileContent(filePath, "UTF-8", fileContent);
	}

	public Object saveFileTemplateContent(Object filePath, Object fileContent)
			throws Exception {
		BaseLog.debug(filePath+"-"+fileContent);
		filePath = "/template" + filePath;
		return this.saveFileContent(filePath, "UTF-8", fileContent);
	}

	public Object saveFileContent(Object filePath, Object fileContent)
			throws Exception {
		return this.saveFileContent(filePath, "UTF-8", fileContent);
	}

	public Object saveFileContent(Object filePath, Object encode,
			Object fileContent) throws Exception {
		if (filePath != null) {
			if (filePath.getClass() == "".getClass()) {
				filePath = BeetlUtil.getWebRoot() + filePath;
				if (encode == null || Const.isEmpty(encode + "")) {
					return FileUtils.saveTextFileContent(filePath + "", "",
							fileContent + "");
				} else {
					return FileUtils.saveTextFileContent(filePath + "", encode
							+ "", fileContent + "");
				}
			}
		}
		throw new Exception("NO FOUND AIM FILE!");
	}
	
	public void setValue(Object[] obj,int index,Object value){
		obj[index]=value;
	}
	

	public static void main(String args[]) throws Exception {
		FileUtilsFunction fileUtils = new FileUtilsFunction();
		FileObject[] fList = fileUtils.getFolderWebRootList("/admin", true);
		for (FileObject f : fList) {
			System.out.println("+=" + f.getName().getBaseName() + ","
					+ KettleVFS.getFilename(f));
		}
	}
}
