package com.ram.kettle.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Comparator;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.cache.WeakRefFilesCache;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.local.LocalFile;

import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.kettle.util.UUIDUtil;

public class KettleVFS {
	private static Class<?> PKG = KettleVFS.class; // for i18n purposes, needed
													// by Translator2!!
													// $NON-NLS-1$

	private static final KettleVFS kettleVFS = new KettleVFS();
	private final DefaultFileSystemManager fsm;

	private KettleVFS() {
		fsm = new StandardFileSystemManager();
		try {
			fsm.setFilesCache(new WeakRefFilesCache());
			fsm.init();
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (fsm != null) {
					fsm.close();
				}
			}
		}));
	}

	public FileSystemManager getFileSystemManager() {
		return fsm;
	}

	public static KettleVFS getInstance() {
		return kettleVFS;
	}

	public static FileObject getFileObject(String vfsFilename)
			throws KettleException {

		try {
			FileSystemManager fsManager = getInstance().getFileSystemManager();

			String filename = vfsFilename;

			FileObject fileObject = null;

			fileObject = fsManager.resolveFile(filename);

			return fileObject;
		} catch (IOException e) {
			throw new KettleException(
					"Unable to get VFS File object for filename '"
							+ vfsFilename + "' : " + e.getMessage());
		}
	}

	public static String getTextFileContent(String vfsFilename,
			String charSetName) throws KettleException {
		try {
			InputStream inputStream = null;

			// if (space == null) {
			inputStream = getInputStream(vfsFilename);
			// } else {
			// inputStream = getInputStream(vfsFilename, space);
			// }
			InputStreamReader reader = new InputStreamReader(inputStream,
					charSetName);
			int c;
			StringBuffer stringBuffer = new StringBuffer();
			while ((c = reader.read()) != -1)
				stringBuffer.append((char) c);
			reader.close();
			inputStream.close();

			return stringBuffer.toString();
		} catch (IOException e) {
			throw new KettleException(e);
		}
	}

	public static boolean fileExists(String vfsFilename) throws KettleException {
		FileObject fileObject = null;
		try {
			fileObject = getFileObject(vfsFilename);
			return fileObject.exists();
		} catch (IOException e) {
			throw new KettleException(e);
		} finally {
			if (fileObject != null) {
				try {
					fileObject.close();
				} catch (Exception e) {
				} 
			}
		}
	}

	public static InputStream getInputStream(FileObject fileObject)
			throws FileSystemException {
		FileContent content = fileObject.getContent();
		return content.getInputStream();
	}

	public static InputStream getInputStream(String vfsFilename)
			throws KettleException {
		try {
			FileObject fileObject = getFileObject(vfsFilename);
			return getInputStream(fileObject);
		} catch (IOException e) {
			throw new KettleException(e);
		}
	}

	public static OutputStream getOutputStream(FileObject fileObject,
			boolean append) throws IOException {
		FileObject parent = fileObject.getParent();
		if (parent != null) {
			if (!parent.exists()) {
				throw new IOException(BaseMessages.getString(PKG,
						"KettleVFS.Exception.ParentDirectoryDoesNotExist",
						getFilename(parent)));
			}
		}
		try {
			fileObject.createFile();
			FileContent content = fileObject.getContent();
			return content.getOutputStream(append);
		} catch (FileSystemException e) {
			// Perhaps if it's a local file, we can retry using the standard
			// File object. This is because on Windows there is a bug in VFS.
			//
			if (fileObject instanceof LocalFile) {
				try {
					String filename = getFilename(fileObject);
					return new FileOutputStream(new File(filename), append);
				} catch (Exception e2) {
					throw e; // throw the original exception: hide the retry.
				}
			} else {
				throw e;
			}
		}
	}

	public static OutputStream getOutputStream(String vfsFilename,
			boolean append) throws KettleException {
		try {
			FileObject fileObject = getFileObject(vfsFilename);
			return getOutputStream(fileObject, append);
		} catch (IOException e) {
			throw new KettleException(e);
		}
	}

	public static String getFilename(FileObject fileObject) {
		FileName fileName = fileObject.getName();
		String root = fileName.getRootURI();
		if (!root.startsWith("file:"))return fileName.getURI(); // nothing we can do about non-normal files. //$NON-NLS-1$
		if (root.startsWith("file:////"))
			return fileName.getURI(); // we'll see 4 forward slashes for a
										// windows/smb network share
		if (root.endsWith(":/")) // Windows //$NON-NLS-1$
		{
			root = root.substring(8, 10);
		} else // *nix & OSX
		{
			root = ""; //$NON-NLS-1$
		}
		String fileString = root + fileName.getPath();
		if (!"/".equals(Const.FILE_SEPARATOR)) //$NON-NLS-1$
		{
			fileString = Const.replace(fileString, "/", Const.FILE_SEPARATOR); //$NON-NLS-1$
		}
		return fileString;
	}

	public static FileObject createTempFile(String prefix, String suffix,
			String directory) throws KettleException {
		try {
			FileObject fileObject;
			do {
				String filename = new StringBuffer(50).append(directory)
						.append('/').append(prefix).append('_')
						.append(UUIDUtil.getUUIDAsString()).append(suffix)
						.toString();
				fileObject = getFileObject(filename);
			} while (fileObject.exists());
			return fileObject;
		} catch (IOException e) {
			throw new KettleException(e);
		}
	}

	public static Comparator<FileObject> getComparator() {
		return new Comparator<FileObject>() {
			public int compare(FileObject o1, FileObject o2) {
				String filename1 = getFilename(o1);
				String filename2 = getFilename(o2);
				return filename1.compareTo(filename2);
			}
		};
	}

	/**
	 * Get a FileInputStream for a local file. Local files can be read with NIO.
	 * 
	 * @param fileObject
	 * @return a FileInputStream
	 * @throws IOException
	 * @deprecated because of API change in Apache VFS. As a workaround use
	 *             FileObject.getName().getPathDecoded(); Then use a regular
	 *             File() object to create a File Input stream.
	 */
	public static FileInputStream getFileInputStream(FileObject fileObject)
			throws IOException {

		if (!(fileObject instanceof LocalFile)) {
			// We can only use NIO on local files at the moment, so that's what
			// we limit ourselves to.
			//
			throw new IOException(BaseMessages.getString(PKG,
					"FixedInput.Log.OnlyLocalFilesAreSupported"));
		}

		return new FileInputStream(fileObject.getName().getPathDecoded());
	}

}
