package com.andy.myself.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;

/**
 * @description: Volley框架测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-4-15  下午12:56:49
 */
public class VolleyActivity extends BaseActivity {
	
	private RequestQueue mRequestQueue;
	private TextView volley_result_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volley);
		mRequestQueue = Volley.newRequestQueue(this);
		
		initView(savedInstanceState);
	}
	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		titleTextView.setText(R.string.activity_volley_title);
		volley_result_tv = (TextView) findViewById(R.id.volley_result_tv);
		
		Button volley_http_get = (Button) findViewById(R.id.volley_http_get);
		volley_http_get.setOnClickListener(new View.OnClickListener() {
			String url = "http://192.168.8.26:8080/AndroidService/getData?param=这是get方式传递过来的参数";
			@Override
			public void onClick(View arg0) {
				StringRequest stringRequest = new StringRequest(Request.Method.GET, url, 
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							volley_result_tv.setText("get方式得到的数据：\n" + response);
						}
					}, 
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							volley_result_tv.setText("get请求失败");
						}
					}
				) {
					@Override  
					public Map<String, String> getHeaders() throws AuthFailureError {  
						Map<String, String> headers = new HashMap<String, String>();  
						headers.put("Charset", "UTF-8");  
						headers.put("Content-Type", "application/json");  
						return headers;  
				   }
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("param", "get请求传递的参数");
						return map;
					}
				};
				
				mRequestQueue.add(stringRequest);
			}
		});
		Button volley_http = (Button) findViewById(R.id.volley_http);
		volley_http.setOnClickListener(new View.OnClickListener() {
			String url = "http://192.168.8.26:8080/AndroidService/getData";
			@Override
			public void onClick(View arg0) {
				
				Header header = new Header();
				header.setCode("1000");
				header.setDesc("post请求");
				header.setSessionId("sessionid");
				header.setStatus("");
				header.setUserId("userId");
				
				String headerString = com.alibaba.fastjson.JSONObject.toJSONString(header);
				
				Map<String, String> paraMap = new HashMap<String, String>();
				paraMap.put("json", headerString);
				
				JSONObject jsonObject = new JSONObject(paraMap);
				
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
					Request.Method.POST, 
					url, 
					jsonObject,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							volley_result_tv.setText("post方式得到的数据：\n" + response.toString());
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							volley_result_tv.setText("post请求失败");
						}
					}
					) {
						@Override  
						public Map<String, String> getHeaders() throws AuthFailureError {  
							Map<String, String> headers = new HashMap<String, String>();  
							headers.put("Charset", "UTF-8");  
							headers.put("Content-Type", "application/json");  
							return headers;  
					   }

						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("param", "参数设置");
							return paramMap;
						}
				};
				mRequestQueue.add(jsonObjectRequest);
			}
		});
	}
	
	class Header {
		private String userId;
		private String sessionId;
		private String code;
		private String status;
		private String desc;
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getSessionId() {
			return sessionId;
		}
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
