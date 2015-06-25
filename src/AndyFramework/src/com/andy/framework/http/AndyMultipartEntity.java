package com.andy.framework.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

/**
 * @description: httpEntity实现
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-8  上午10:50:52
 */
public class AndyMultipartEntity implements HttpEntity {

	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	
	// 边界
	private String boundary = null;
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	private boolean isSetLast = false;
	private boolean isSetFirst = false;
	
	public AndyMultipartEntity() {
		StringBuffer stringBuffer = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 30; i++) {
			stringBuffer.append(MULTIPART_CHARS[random.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = stringBuffer.toString();
	}
	
	public void writeFirstBoundaryIfNeed() {
		if (!isSetFirst) {
			try {
				out.write(("--" + boundary + "\r\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isSetFirst = true;
	}
	
	public void writeLastBoundaryIfNeed() {
		if (!isSetLast) {
			try {
				out.write(("\r\n--" + boundary + "--\r\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isSetLast = true;
	}
	
	/**
	 * 添加字符串
	 * @param key
	 * @param value
	 * */
	public void addPart(final String key, final String value) {
		writeFirstBoundaryIfNeed();
		try {
			out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
			out.write(value.getBytes());
			out.write(("\r\n--" + boundary + "\r\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加文件流
	 * @param key
	 * @param fileName 文件名
	 * @param inputStream 输入流
	 * */
	public void addPart(final String key, final String fileName, final InputStream inputStream, String type, final boolean isLast){
		writeFirstBoundaryIfNeed();
        try {
            type = "Content-Type: " + type + "\r\n";
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
            out.write(type.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4096];
            int len = 0;
            while ((len = inputStream.read(tmp)) != -1) {
                out.write(tmp, 0, len);
            }
            if(!isLast)
                out.write(("\r\n--" + boundary + "\r\n").getBytes());
            out.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
            	inputStream.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void addPart(final String key, final String fileName, final InputStream fin, final boolean isLast){
        addPart(key, fileName, fin, "application/octet-stream", isLast);
    }
	
	/**
	 * addPart
	 * @param key
	 * @param file File type
	 * @param isLast
	 * */
	public void addPart(final String key, final File file, final boolean isLast) {
        try {
            addPart(key, file.getName(), new FileInputStream(file), isLast);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
	
/***********************************  Override method  ***********************************************************/	
	@Override
	public void consumeContent() throws IOException,UnsupportedOperationException {
		if (isStreaming()) {
            throw new UnsupportedOperationException(
            "Streaming entity does not implement #consumeContent()");
        }
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException,UnsupportedOperationException {
		return new ByteArrayInputStream(out.toByteArray());
	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public long getContentLength() {
		writeLastBoundaryIfNeed();
		return out.toByteArray().length;
	}

	@Override
	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		outstream.write(out.toByteArray());
	}

}
