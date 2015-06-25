package com.andy.framework.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @description: 可保存到sharedPreferences的CookieStore
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-8  下午5:14:09
 */
@SuppressLint("DefaultLocale")
public class AndyPreferencesCookieStore implements CookieStore {

	private static final String COOKIE_PREFIX = "CookiePrefsFile";
    private static final String COOKIE_NAME_STORE = "names";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private static final String COOKIE_SPLIT = ",";
    
    private SharedPreferences sp = null;
    private ConcurrentHashMap<String, Cookie> cookies;
    
	/**
	 * 构造函数，初始化sp,cookies
	 * */
	public AndyPreferencesCookieStore(Context context) {
		sp = context.getSharedPreferences(COOKIE_PREFIX, Context.MODE_PRIVATE);
		cookies = new ConcurrentHashMap<String, Cookie>();
		
		String cookieNameString = sp.getString(COOKIE_NAME_STORE, null);
		if (cookieNameString != null) {
			String[] cookieNames = TextUtils.split(cookieNameString, COOKIE_SPLIT);
			for (String cookieName : cookieNames) {
				String cookieString = sp.getString(cookieName, null);
				if (cookieString != null) {
					Cookie cookie = decodeCookie(cookieString);
					if (cookie != null) {
						cookies.put(cookieName, cookie);
					}
				}
			}
		}
		clearExpired(new Date());
	}
	
	/**
	 * 序列化cookie
	 * */
	protected String encodeCookie(AndySerializableCookie cookie) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objos = new ObjectOutputStream(os);
			objos.writeObject(cookie);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArrayToHexString(os.toByteArray());
	}
	
	/**
	 * 反序列化cookie
	 * */
	protected Cookie decodeCookie(String cookieStr) {
		byte[] bytes = hexStringToByteArray(cookieStr);
		
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		Cookie cookie = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			cookie = ((AndySerializableCookie)ois.readObject()).getCookie();
		} catch (Exception e) {
           e.printStackTrace();
        }
		return cookie;
	}
	
	/**
	 * 将字节数组转化成大写string
	 * */
	private String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
        for (byte element : b) {
            int v = element & 0xff;
            if(v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
	}
	
	/**
	 * 将String转换成字节数组
	 * */
	protected byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
	
	
	@Override
	public void addCookie(Cookie cookie) {
		String name = cookie.getName();
		if (!cookie.isExpired(new Date())) {
			cookies.put(name, cookie);
		} else {
			cookies.remove(name);
		}
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(COOKIE_NAME_STORE, TextUtils.join(COOKIE_SPLIT, cookies.keySet()));
		editor.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new AndySerializableCookie(cookie)));
		editor.commit();
	}

	@Override
	public void clear() {
		SharedPreferences.Editor editor = sp.edit();
		for (String nameString : cookies.keySet()) {
			editor.remove(COOKIE_NAME_PREFIX + nameString);
		}
		editor.remove(COOKIE_NAME_STORE);
		editor.commit();
		cookies.clear();
	}

	@Override
	public boolean clearExpired(Date date) {
		boolean cleared = false;
		SharedPreferences.Editor editor = sp.edit();
		for (ConcurrentHashMap.Entry<String, Cookie> entry : cookies.entrySet()) {
			String name = entry.getKey();
			Cookie cookie = entry.getValue();
			if (cookie.isExpired(new Date())) {
				cookies.remove(name);
				editor.remove(COOKIE_NAME_PREFIX + name);
				cleared = true;
			}
		}
		if (cleared) {
			editor.putString(COOKIE_NAME_STORE, TextUtils.join(COOKIE_SPLIT, cookies.keySet()));
		}
		editor.commit();
		return cleared;
	}

	@Override
	public List<Cookie> getCookies() {
		return new ArrayList<Cookie>(cookies.values());
	}

	/**
	 * 可序列化的cookie
	 * 用于保存cookie对象
	 * */
	public class AndySerializableCookie implements Serializable {
		private static final long serialVersionUID = 6374381828722046732L;
		
		private transient final Cookie cookie;
		private transient BasicClientCookie clientCookie;
		
		public AndySerializableCookie(Cookie cookie) {
			this.cookie = cookie;
		}
		
		public Cookie getCookie() {
			if (clientCookie != null) {
				return clientCookie;
			}
			return cookie;
		}
		
		public void writeObject(ObjectOutput out) throws IOException {
			out.writeObject(cookie.getName());
            out.writeObject(cookie.getValue());
            out.writeObject(cookie.getComment());
            out.writeObject(cookie.getDomain());
            out.writeObject(cookie.getExpiryDate());
            out.writeObject(cookie.getPath());
            out.writeInt(cookie.getVersion());
            out.writeBoolean(cookie.isSecure());
		}
		
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            String name = (String)in.readObject();
            String value = (String)in.readObject();
            clientCookie = new BasicClientCookie(name, value);
            clientCookie.setComment((String)in.readObject());
            clientCookie.setDomain((String)in.readObject());
            clientCookie.setExpiryDate((Date)in.readObject());
            clientCookie.setPath((String)in.readObject());
            clientCookie.setVersion(in.readInt());
            clientCookie.setSecure(in.readBoolean());
        }
	}
}
