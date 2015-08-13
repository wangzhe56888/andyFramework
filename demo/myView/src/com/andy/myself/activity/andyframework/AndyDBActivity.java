package com.andy.myself.activity.andyframework;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andy.framework.sqlite.AndyDB;
import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * @description: db的使用测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-29  上午10:56:01
 */
public class AndyDBActivity extends BaseHeaderActivity {
	private TextView resultTextView;
	private EditText userNameEditText, passwordEditText;
	private Button insertOrUpdateBtn, selectBtn, deleteBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db);
		initView(savedInstanceState);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		resultTextView = (TextView) findViewById(R.id.db_result_tv_id);
		userNameEditText = (EditText) findViewById(R.id.db_username_et_id);
		passwordEditText = (EditText) findViewById(R.id.db_password_et_id);
		insertOrUpdateBtn = (Button) findViewById(R.id.db_insert_update_btn_id);
		selectBtn = (Button) findViewById(R.id.db_select_btn_id);
		deleteBtn = (Button) findViewById(R.id.db_delete_btn_id);
		
		insertOrUpdateBtn.setOnClickListener(this);
		selectBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		
		updateResult("初始数据：");
	}
	
	private void updateResult(String titleString) {
		AndyDB andyDB = AndyDB.create(mActivity);
		List<DBUserBean> beans = andyDB.findAll(DBUserBean.class);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(titleString);
		for (DBUserBean bean : beans) {
			stringBuffer.append(bean.getId()).append(" - ")
			.append(bean.getUsername()).append(" - ")
			.append(bean.getPassword()).append("\n");
		}
		resultTextView.setText(stringBuffer.toString());
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.db_insert_update_btn_id:
		{
			AndyDB andyDB = AndyDB.create(mActivity);
			String nameString = userNameEditText.getText().toString();
			String pwdString = passwordEditText.getText().toString();
			DBUserBean bean = new DBUserBean();
			bean.setUsername(nameString);
			bean.setPassword(pwdString);
//			andyDB.save(bean);
			andyDB.saveBindId(bean);
			updateResult("插入/更新：");
			break;
		}
		case R.id.db_select_btn_id:
			updateResult("查询：");
			break;
		case R.id.db_delete_btn_id:
			AndyDB andyDB = AndyDB.create(mActivity);
			andyDB.deleteAll(DBUserBean.class);
			updateResult("删除所有数据：");
			break;

		default:
			super.onClick(v);
			break;
		}
	}
}
