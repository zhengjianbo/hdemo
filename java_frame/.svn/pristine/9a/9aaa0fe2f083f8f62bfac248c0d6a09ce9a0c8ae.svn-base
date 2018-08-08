package com.ram.kettle.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.vfs.FileObject;

import com.ram.kettle.log.KettleException;
import com.ram.kettle.xml.KettleVFS;

public class FileUtils {

	public static String TEMPFOLDER = "";
	public static String TEMPLATEFOLDER = "TEMPLATE";

	public static String getFileFromTemplate(String tempFile,
			Map<String, String> replaceVar) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		String content = "";
		try {
			String outFile = TEMPFOLDER + UUID.randomUUID().toString();

			br = new BufferedReader(new FileReader(tempFile));
			bw = new BufferedWriter(new FileWriter(outFile));
			String line;
			while ((line = br.readLine()) != null && (line != "")) {
				content = line + "\r\n";
				for (Map.Entry<String, String> entry : replaceVar.entrySet()) {
					content = content.replaceAll(entry.getKey(),
							entry.getValue());
				}
				bw.write(content);
			}
			bw.flush();
			return outFile;
		} catch (Exception e) {
		} finally {
			try {
				if (br != null)
					br.close();
				if (bw != null)
					bw.close();
			} catch (Exception e) {

			}
		}
		return "";
	}

	public static String getFileListFolder(String path) {
		Map<String, String> replaceVar = new HashMap<String, String>();
		replaceVar.put("#FOLDER#", path);
		String dxlFile = getFileFromTemplate(TEMPLATEFOLDER + "/" + "LIST.DXL",
				replaceVar);
		return dxlFile;
	}

	public static String getExportModuleHtml(String ModulePath) {
		Map<String, String> replaceVar = new HashMap<String, String>();
		replaceVar.put("#MODULE_PATH#", ModulePath);
		String dxlFile = getFileFromTemplate(TEMPLATEFOLDER + "/"
				+ "EXPORT_MODULE_HTML.DXL", replaceVar);
		return dxlFile;
	}

	public static void main(String args[]) {
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static boolean saveTextFileContent(String vfsFilename,
			String encoding, String htmlContent) throws Exception {

		createParentFolder(vfsFilename, true);

		OutputStream outputStream;

		OutputStream fos = KettleVFS.getOutputStream(vfsFilename, false);
		outputStream = fos;

		OutputStreamWriter writer = new OutputStreamWriter(
				new BufferedOutputStream(outputStream, 5000));

		if (!Const.isEmpty(encoding)) {
			writer = new OutputStreamWriter(new BufferedOutputStream(
					outputStream, 5000), encoding);
		} else {
			writer = new OutputStreamWriter(new BufferedOutputStream(
					outputStream, 5000));
		}

		writer.write(htmlContent);

		if (writer != null) {
			writer.close();
			writer = null;
		}

		if (fos != null) {
			fos.close();
			fos = null;
		}
		return true;

	}

	public static String getTextFileContent(String vfsFilename, String encoding)
			throws KettleException {
//		System.out.println("vfsFilename:" + vfsFilename);
		InputStream inputStream = null;
		InputStreamReader reader = null;

		String retval = null;
		try {

			File f = new File(vfsFilename);
			if (f.exists() && f.isFile()) {

			} else {
				return "";
			}

			inputStream = KettleVFS.getInputStream(vfsFilename);

			if (!Const.isEmpty(encoding)) {
				reader = new InputStreamReader(new BufferedInputStream(
						inputStream), encoding);
			} else {
				reader = new InputStreamReader(new BufferedInputStream(
						inputStream));
			}

			int c;
			StringBuffer stringBuffer = new StringBuffer();
			while ((c = reader.read()) != -1)
				stringBuffer.append((char) c);

			retval = stringBuffer.toString();
		} catch (Exception e) {
			throw new KettleException("LoadFileInput.Error.GettingFileContent"
					+ vfsFilename + "," + e.toString());
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (Exception e) {
				}
			;
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			;
		}

		return retval;
	}

	public static void createParentFolder(String filename,
			boolean isCreateParentFolder) throws Exception {
		FileObject parentfolder = null;
		try {
			parentfolder = KettleVFS.getFileObject(filename).getParent();
			if (parentfolder.exists()) {
			} else {
				if (isCreateParentFolder) {
					parentfolder.createFolder();
				} else {
					throw new KettleException(
							"TextFileOutput.Log.ParentFolderNotExistCreateIt");
				}
			}
		} finally {
			if (parentfolder != null) {
				try {
					parentfolder.close();
				} catch (Exception ex) {
				}
			}
		}
	}

}