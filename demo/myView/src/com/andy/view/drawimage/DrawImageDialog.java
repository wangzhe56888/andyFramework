package com.andy.view.drawimage;

import com.andy.myself.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

public class DrawImageDialog extends Dialog {

	public interface DrawImageDialogListener {
		public void callback(Object object);
	}
	
	private Context context;
	private LayoutParams p ;
	private DrawImageDialogListener dialogListener;
	PaintView mView;
    
    public DrawImageDialog(Context context,DrawImageDialogListener dialogListener) {
        super(context);
        this.context = context;
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.andy_layout_draw_image);
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        p = getWindow().getAttributes();
        p.height = metrics.heightPixels / 2;
        p.width = metrics.widthPixels;
        getWindow().setAttributes(p);
        
        
        mView = new PaintView(context, p);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.draw_area_view);
        frameLayout.addView(mView);
        mView.requestFocus();
        Button btnClear = (Button) findViewById(R.id.draw_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                 mView.clear();
            }
        });

        Button btnOk = (Button) findViewById(R.id.draw_finished);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dialogListener.callback(mView.getCachebBitmap());
                    DrawImageDialog.this.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        Button btnCancel = (Button)findViewById(R.id.draw_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
}