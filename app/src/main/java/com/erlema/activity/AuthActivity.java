package com.erlema.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.bmob.btp.callback.UploadListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlema.bean.MyUser;
import com.erlema.util.MyUtils;
import com.example.erlema.R;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;

public class AuthActivity extends BaseActivity {
    TextView tv_status;
    ImageView iv_auth;
    private MyUser loginUer;
    private static final int MEDIA_TYPE_IMAGE = 1;
    String pathImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_auth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initViews() {
        iv_auth = (ImageView) findViewById(R.id.iv_auth);
        tv_status = (TextView) findViewById(R.id.tv_status);
    }

    private Uri fileUri;

    @Override
    public void initListeners() {
        iv_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loginUer.getAuthed()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = MyUtils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                    // 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
                    // set the image file name
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, 2);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            // 如果是拍照
            if (RESULT_OK == resultCode) {
                // Check if the result includes a thumbnail Bitmap
                if (data != null) {
                    // 没有指定特定存储路径的时候
                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
                    // Image captured and saved to fileUri specified in the
                    // Intent
                    // Toast.makeText(this,
                    // "Image saved to:\n" + data.getData(),
                    // Toast.LENGTH_LONG).show();
                    if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                    }
                } else {

                    // If there is no thumbnail image data, the image
                    // will have been stored in the target output URI.

                    // Resize the full image to fit in out image view.
                    int width = 600;
                    int height = 450;

                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

                    factoryOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);

                    int imageWidth = factoryOptions.outWidth;
                    int imageHeight = factoryOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(imageWidth / width, imageHeight
                            / height);

                    // Decode the image file into a Bitmap sized to fill the
                    // View
                    factoryOptions.inJustDecodeBounds = false;
                    factoryOptions.inSampleSize = scaleFactor;
                    factoryOptions.inPurgeable = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                            factoryOptions);
                    pathImage = fileUri.getPath();
                    if (pathImage != null) {
                        Glide.with(AuthActivity.this).load(pathImage).override(300, 300).into(iv_auth);
                        uploadThubnail(pathImage);

                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    @Override
    protected void onResume() {
        if (loginUer.getAuth_img() != null) {
            tv_status.setText("当前状态：已上传，审核中");
        } else {
            tv_status.setText("当前状态：未上传");
        }
        super.onResume();
    }

    @Override
    public void initData() {
        updateLoginUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AuthActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLoginUser() {
        loginUer = BmobUser.getCurrentUser(getApplicationContext(), MyUser.class);
        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(getApplicationContext(), loginUer.getObjectId(), new GetListener<MyUser>() {
            @Override
            public void onFailure(int i, String s) {
                Snackbar.make(tv_status, "当前网络异常，请稍候再试", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(MyUser myUser) {
                loginUer.setSchool(myUser.getSchool());
                loginUer.setAvator(myUser.getAvator());
                loginUer.setCity(myUser.getCity());
                loginUer.setCredibility(myUser.getCredibility());
                loginUer.setGender(myUser.getGender());
                loginUer.setrelName(myUser.getrelName());
                loginUer.setStatement(myUser.getStatement());
                loginUer.setAuthed(myUser.getAuthed());
                loginUer.update(getApplicationContext());
                if (myUser.getAuth_img() != null) {
                    tv_status.setText("当前状态：已上传，审核中");
                } else {
                    tv_status.setText("当前状态：未上传");
                }
                if (myUser.getAuthed()){
                    tv_status.setText("当前状态：审核通过");
                }
                getSupportActionBar().setTitle("欢迎认证，"+myUser.getrelName()+"同学");
                Glide.with(AuthActivity.this).load(loginUer.getAuth_img()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.iconfont_adds).into(iv_auth);
            }
        });
    }


    /**
     * 上传缩略图
     *
     * @param s
     */
    private void uploadThubnail(String s) {
        BTPFileResponse response = BmobProFile.getInstance(getApplicationContext()).upload(s, new UploadListener() {

            @Override
            public void onSuccess(String fileName, String url, BmobFile file) {
                Log.i("bmob", "文件上传成功：" + fileName + ",可访问的文件地址：" + file.getUrl());
                Glide.with(getApplicationContext()).load(file.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_auth);
                tv_status.setText("当前状态：已上传，审核中");
                loginUer.setAuth_img(file.getUrl());
                loginUer.update(getApplicationContext());
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgress(int progress) {
                // TODO Auto-generated method stub
                Log.i("bmob", "onProgress :" + progress);
                ShowToast("图片上传中，进度：" + progress + "%");
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
                Log.i("bmob", "文件上传失败：" + errormsg);
            }
        });
    }
}
