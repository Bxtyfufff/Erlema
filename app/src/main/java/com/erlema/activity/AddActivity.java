package com.erlema.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.erlema.bean.MyGoods;
import com.erlema.bean.MyUser;
import com.erlema.bean.TagChexkBox;
import com.erlema.bean.myActionBar;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.LayoutRipple;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

public class AddActivity extends BaseActivity implements OnClickListener {
    private ButtonFloat btAdd;
    private ButtonRectangle submit;
    private myActionBar myactionbar;
    private GridView gridView1; // 网格显示缩略图
    private static final int CAPTURE_OPEN = 0;// 打开相机标记
    private static final int MEDIA_TYPE_IMAGE = 1;
    private final int IMAGE_OPEN = 1; // 打开图片标记
    private String pathImage; // 选择图片路径
    private Bitmap bmp; // 导入临时图片
    private List<String> filePathList = new ArrayList<String>();// 上传文件路径
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter; // 适配器
    EditText title, price, describe;
    Spinner cate, degree;
    String mtitle, mprice, mdescribe, mcate, mdegree;
    TagChexkBox tag0, tag1, tag2, tag3;
    TagChexkBox[] tags;
    private TextInputLayout ti_title, ti_price, ti_describe;
    private MyGoods good_upload;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_add);
    }

    @Override
    public void initViews() {
        // btAdd = (ButtonFloat) findViewById(R.id.add_img_add);
        // myactionbar=(myActionBar) findViewById(R.id.myActionBar);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        submit = (ButtonRectangle) findViewById(R.id.btr_submit);
        title = (EditText) findViewById(R.id.add_title);
        price = (EditText) findViewById(R.id.add_price);
        describe = (EditText) findViewById(R.id.add_describe);
        cate = (Spinner) findViewById(R.id.add_cate_sp);
        degree = (Spinner) findViewById(R.id.add_degree);
        tag0 = (TagChexkBox) findViewById(R.id.tag0);
        tag1 = (TagChexkBox) findViewById(R.id.tag1);
        tag2 = (TagChexkBox) findViewById(R.id.tag2);
        tag3 = (TagChexkBox) findViewById(R.id.tag3);
        tags = new TagChexkBox[]{tag0, tag1, tag2, tag3};

        ti_title = (TextInputLayout) findViewById(R.id.ti_title);
        ti_describe = (TextInputLayout) findViewById(R.id.ti_describe);
        ti_price = (TextInputLayout) findViewById(R.id.ti_price);
    }

    @Override
    public void initListeners() {
        // btAdd.setOnClickListener(this);
        tag0.setOnClickListener(this);
        submit.setOnClickListener(this);
        /*
         * 监听GridView点击事件 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
		 */
        gridView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (imageItem.size() == 10) { // 第一张为默认图片
                    Toast.makeText(AddActivity.this, "图片数9张已满",
                            Toast.LENGTH_SHORT).show();
                } else if (position == imageItem.size() - 1) { // 点击图片位置为+
                    // 0对应0张图片
                    // 选择图片
                    LinearLayout v1 = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.activity_camera, null);
                    LayoutRipple lr1 = (LayoutRipple) v1
                            .findViewById(R.id.LayoutRipple01);
                    lr1.setRippleColor(Color.parseColor("#1E88E5"));
                    lr1.setOnClickListener(AddActivity.this);
                    LayoutRipple lr11 = (LayoutRipple) v1
                            .findViewById(R.id.LayoutRipple02);
                    lr11.setRippleColor(Color.parseColor("#1E88E5"));
                    lr11.setOnClickListener(AddActivity.this);
                    Builder builder = new Builder(AddActivity.this);
                    builder.setView(v1);
                    ad = builder.create();
                    ad.show();
                    // 通过onResume()刷新数据
                } else {
                    // dialog(position);
                    // Toast.makeText(MainActivity.this, "点击第" + (position + 1)
                    // + " 号图片",
                    // Toast.LENGTH_SHORT).show();
                }

            }
        });
        gridView1.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                dialog(position);
                return false;
            }
        });
    }

    @Override
    public void initData() {
        bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.gridview_addpic); // 加号
        imageItem = new ArrayList<HashMap<String, Object>>();
        SetAddIco();
        simpleAdapter = new SimpleAdapter(this, imageItem,
                R.layout.griditem_addpic, new String[]{"itemImage"},
                new int[]{R.id.imageView1});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如 map.put("itemImage",
		 * R.drawable.img); 解决方法: 1.自定义继承BaseAdapter实现 2.ViewBinder()接口实现 参考
		 * http://blog.csdn.net/admin_/article/details/7257901
		 */
        simpleAdapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);
    }

    private void SetAddIco() {
        /*
         * 载入默认图片添加图片加号 通过适配器实现 SimpleAdapter参数imageItem为数据源
		 * R.layout.griditem_addpic为布局
		 */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 打开图片
        if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                // 查询选择图片
                Cursor cursor = getContentResolver().query(uri,
                        new String[]{MediaStore.Images.Media.DATA}, null,
                        null, null);
                // 返回 没找到选择图片
                if (null == cursor) {
                    MyUtils.showLog("imageop", "  fsf  null");
                    return;
                }
                // 光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                MyUtils.showLog("imageop", pathImage + "");
            } else {
                pathImage = data.getData().getEncodedPath();
                MyUtils.showLog("imageop", "  else" + pathImage);
            }
        } // end if 打开图片
        if (resultCode == RESULT_OK && requestCode == 100) {
        } else {
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
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    // 刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(pathImage)) {
            filePathList.add(pathImage);// 添加上传图片路径
            Bitmap addbmp = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(pathImage), 80, 80,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.remove(imageItem.size() - 1);
            imageItem.add(map);
            SetAddIco();
            simpleAdapter = new SimpleAdapter(this, imageItem,
                    R.layout.griditem_addpic, new String[]{"itemImage"},
                    new int[]{R.id.imageView1});
            simpleAdapter.setViewBinder(new ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            // 刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }

    AlertDialog ad = null;
    String temp = null;
    private Uri fileUri;

    @Override
    public void onClick(View v) {
        Builder builder;
        switch (v.getId()) {
            case R.id.tag0:// 免费标签
                if (tag0.getSelect()) {
                    temp = price.getText().toString();
                    price.setText("0");
                    ti_price.setHint("");
                    tag1.setEnabled(false);
                    price.setEnabled(false);
                } else {
                    if (temp != null) {
                        tag1.setEnabled(true);
                        ti_price.setHint("请输入价格");
                        price.setText(temp);
                        price.setEnabled(true);
                    }
                }
                break;
            case R.id.btr_submit:
                if (checkInfo()) {
                    uploadFiles();
                    AddActivity.this.finish();
                }
                break;
            case R.id.LayoutRipple01:// 照相
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = MyUtils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                // 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
                // set the image file name
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_OPEN);
                ad.dismiss();
                break;
            case R.id.LayoutRipple02:// 相册
                Intent intent1 = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1, IMAGE_OPEN);
                ad.dismiss();
                break;
            default:
                break;
        }
    }


    ProgressBarCircularIndeterminate bar;
    Dialog dig;

    private void uploadFiles() {
        if (filePathList.size() == 0) {
            return;
        }
        Builder builder = new Builder(AddActivity.this);
        View view = LayoutInflater.from(AddActivity.this).inflate(
                R.layout.process_dialog, null);
        builder.setView(view);
        ShowToast("发布中。。。");
        bar = (ProgressBarCircularIndeterminate) view
                .findViewById(R.id.progressBarCircularIndetermininate);
        builder.setTitle("请稍等");
        dig = builder.create();
        // dig.show();
        dig.setCancelable(false);
        String[] filespath = filePathList.toArray(new String[filePathList
                .size()]);
        BmobProFile.getInstance(getApplicationContext()).uploadBatch(filespath,
                new com.bmob.btp.callback.UploadBatchListener() {

                    @Override
                    public void onSuccess(boolean isFinish, String[] fileNames,
                                          String[] urls, BmobFile[] files) {
                        // isFinish ：批量上传是否完成
                        // fileNames：文件名数组
                        // urls : url：文件地址数组
                        // files : BmobFile文件数组，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
                        // 注：若上传的是图片，url(s)并不能直接在浏览器查看（会出现404错误），需要经过`URL签名`得到真正的可访问的URL地址,当然，`V3.4.1`版本可直接从BmobFile中获得可访问的文件地址。
                        if (isFinish) {
                            String[] imgurls = new String[files.length];
                            for (int i = 0; i < files.length; i++) {
                                imgurls[i] = files[i].getUrl();
                                // Log.i("afas", imgurls[i]+"  0");
                                // Log.i("afas", files[i].getUrl() + "    " +
                                // i);
                            }
                            MyUser local = BmobUser.getCurrentUser(getApplication(), MyUser.class);
                            good_upload = new MyGoods();
                            good_upload.setCate(cate.getSelectedItemPosition());
                            good_upload.setTitle(mtitle);
                            good_upload.setPirce(Double.valueOf(mprice));
                            good_upload.setDegree(degree
                                    .getSelectedItemPosition());
                            good_upload.setDescrib(mdescribe);
                            good_upload.setOwerID(local.getObjectId());
                            good_upload.setImgUrl(imgurls);
                            good_upload.setSchool(local.getSchool());
                            for (int j = 0; j < 4; j++) {
                                if (tags[j].getSelect()) {
                                    good_upload.addTag(tags[j].getText()
                                            .toString());
                                }
                            }
                            good_upload.save(getApplicationContext(),
                                    new SaveListener() {

                                        @Override
                                        public void onSuccess() {
                                            ShowToast("发送成功");
                                            dig.dismiss();
                                        }

                                        @Override
                                        public void onFailure(int arg0,
                                                              String arg1) {
                                            ShowToast("发布失败，请检查网络");
                                            dig.dismiss();
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onProgress(int curIndex, int curPercent,
                                           int total, int totalPercent) {
                        // curIndex :表示当前第几个文件正在上传
                        // curPercent :表示当前上传文件的进度值（百分比）
                        // total :表示总的上传文件数
                        // totalPercent:表示总的上传进度（百分比）
                        Log.i("bmob", "onProgress :" + curIndex + "---"
                                + curPercent + "---" + total + "----"
                                + totalPercent);
                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        // TODO Auto-generated method stub
                        Log.i("bmob", "批量上传出错：" + statuscode + "--" + errormsg);
                        ShowToast("上传失败( ▼-▼ ");
                        dig.dismiss();
                    }
                });
    }

    /*
     * Dialog对话框提示用户删除操作 position为删除图片位置
     */
    protected void dialog(final int position) {

        if (position >= 0 && position < simpleAdapter.getCount() - 1) {
            Builder builder = new Builder(AddActivity.this);
            builder.setMessage("确认移除已添加图片吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            imageItem.remove(position);
                            filePathList.remove(position);
                            simpleAdapter.notifyDataSetChanged();
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    /**
     * @param
     * @return boolean 返回类型
     * @throws
     * @Title: checkInfo
     * @Description: TODO(上传前确认无空白信息)
     */
    private boolean checkInfo() {
        if (filePathList.size() == 0) {
            ShowToast("至少选择一张图片！");
            return false;
        }
        mtitle = title.getText().toString();
        mprice = price.getText().toString();
        mdescribe = describe.getText().toString();
        mcate = getResources().getStringArray(R.array.cateArray)[cate
                .getSelectedItemPosition()];
        mdegree = getResources().getStringArray(R.array.degreeArray)[degree
                .getSelectedItemPosition()];
        if (mtitle == null || mtitle.toString().trim().equals("")) {
            ti_title.setError("标题不能为空");
            return false;
        }
        if (mprice == null || mprice.toString().trim().equals("")) {
            ti_price.setError("价格不能为空");
            return false;
        }
        if (mdescribe == null || mdescribe.toString().trim().equals("")) {
            ti_describe.setError("描述不能为空");
            return false;
        }
        return true;

    }
}
