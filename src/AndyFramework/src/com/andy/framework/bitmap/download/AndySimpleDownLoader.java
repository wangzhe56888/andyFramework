package com.andy.framework.bitmap.download;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import android.annotation.SuppressLint;
import com.andy.framework.util.AndyLog;

/**
 * @description: 根据 图片url地址下载图片, 可以是本地和网络
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  上午9:14:25
 */
public class AndySimpleDownLoader implements AndyDownLoader {

	private static final int IO_BUFFER_SIZE = 8 * 1024;
	
	@SuppressLint("DefaultLocale")
	@Override
	public byte[] download(String urlString) {
		if (urlString == null) {
			return null;
		}
		
		if (urlString.trim().toLowerCase().startsWith("http")) {
			return getFromHttp(urlString);
		} else if (urlString.trim().toLowerCase().startsWith("file:")) {
			try {
				File file = new File(new URI(urlString));
				if (file.exists() && file.canRead()) {
					return getFromFile(file);
				}
			} catch (URISyntaxException e) {
				AndyLog.error_bitmap("Error in read from file - " + urlString + " : " + e);
			}
		} else {
			File file = new File(urlString);
			if (file.exists() && file.canRead()) {
				return getFromFile(file);
			}
		}
		
		return null;
	}

	/**
	 * 读取网络文件
	 * */
	private byte[] getFromHttp(String urlString) {
		HttpURLConnection urlConnection = null;
		FlushedInputStream flushedInputStream = null;
		ByteArrayOutputStream baos = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection)url.openConnection();
			BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
			flushedInputStream = new FlushedInputStream(bis);
			
			baos = new ByteArrayOutputStream();
			int b = 0;
			while ((b = flushedInputStream.read()) != -1) {
				baos.write(b);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			AndyLog.error_bitmap("Error in downloadBitmap - " + urlString + " : " + e);
		} finally {
			urlConnection.disconnect();
			urlConnection = null;
			try {
				if (flushedInputStream != null) {
					flushedInputStream.close();
					flushedInputStream = null;
				}
				if (baos != null) {
					baos.close();
					baos = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * 读取本地文件
	 * */
	private byte[] getFromFile(File file) {
		if (file == null) {
			return null;
		}
		FileInputStream fis = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			fis = new FileInputStream(file);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (Exception e) {
			AndyLog.error_bitmap("Error in read from file - " + file + " : " + e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
				if (bos != null) {
					bos.close();
					bos = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public class FlushedInputStream extends FilterInputStream {

		protected FlushedInputStream(InputStream in) {
			super(in);
		}

		@Override
		public long skip(long byteCount) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < byteCount) {
				long bytesSkipped = in.skip(byteCount - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int mByte = read();
					if (mByte < 0) {
						break;
					} else {
						bytesSkipped = 1;
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
