package com.andy.view.drawimage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.andy.myself.util.PromptUtil;
import com.andy.view.drawimage.DrawImageDialog.DrawImageDialogListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class DrawImageView extends ImageView {

	private String imgPath;
//    private String imgName;
//    private String imgUrl;
    private Context context;
    private Bitmap mSignBitmap;
    
    public DrawImageView(Context context) {
        super(context);
        initView(context);
    }
    
    public DrawImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        this.context = context;
        setOnClickListener(new SignImgListener());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    
    class SignImgListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            DrawImageDialog writeTabletDialog = new DrawImageDialog(
                    context, new DrawImageDialogListener() {
                        @Override
                        public void callback(Object object) {                            
                            mSignBitmap = (Bitmap) object;
                            imgPath = createFile();
                            PromptUtil.showToast(context, "�ļ�����Ϊ��" + imgPath);
                            BitmapDrawable bd=new BitmapDrawable(mSignBitmap);
                            DrawImageView.this.setImageBitmap(mSignBitmap);
                        }
                    });
            writeTabletDialog.show();
        }
        private String createFile() {
            ByteArrayOutputStream baos = null;
            String _path = null;
            try {
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "andyTest" + File.separator;           
                _path = sign_dir + System.currentTimeMillis() + ".jpg";
                
                File file = new File(sign_dir);
                if (!file.exists()) {
                	file.mkdirs();
                }
                
                baos = new ByteArrayOutputStream();
                mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] photoBytes = baos.toByteArray();
                if (photoBytes != null) {
                    new FileOutputStream(new File(_path)).write(photoBytes);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } finally {
                try {
                    if (baos != null)
                        baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return _path;
        }  
    }
}
