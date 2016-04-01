package com.erlema.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.erlema.activity.ChatActivity;
import com.erlema.activity.GoodsDetailsActivity;
import com.erlema.activity.WantedGoodsDetailsActivity;
import com.erlema.bean.Comments;
import com.erlema.bean.MyUser;
import com.erlema.config.Constants;
import com.erlema.util.MyUtils;
import com.example.erlema.R;
import com.gc.materialdesign.views.ButtonIcon;

import org.json.JSONException;
import org.json.JSONObject;

public class ToolbarFragmentWrite extends Fragment {
    private ButtonIcon tool_confirm;
    private EditText content_et;
    private String content;
    private MyUser localuser;
    private Activity parentAct;
    private String userId, userName, sellerName, answerTo, goodsId;
    private int action = 0;//推送跳转指令

    public static ToolbarFragmentWrite newInstance(String userId, String userName,
                                                   String answerTo, String goodsId) {
        ToolbarFragmentWrite fragment = new ToolbarFragmentWrite();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("userName", userName);
        bundle.putString("answerTo", answerTo);
        bundle.putString("goodsId", goodsId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.bottom_tool_bar_write,
                container, false);

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            userName = getArguments().getString("userName");
            answerTo = getArguments().getString("answerTo");
            goodsId = getArguments().getString("goodsId");
        }
        if (parentAct instanceof GoodsDetailsActivity) {
            action = Constants.ACTION_GOODSDETAIL;
        }
        if (parentAct instanceof WantedGoodsDetailsActivity) {
            action = Constants.ACTION_WGOODSDETAIL;
            answerTo = ((WantedGoodsDetailsActivity) parentAct)
                    .getAnswerTo();
        }
        if (answerTo != null) {
            if (content_et != null) {
                content_et.setHint("回复:" + answerTo);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void initViews() {
        content_et = (EditText) getActivity().findViewById(
                R.id.tool_content);
        localuser = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        tool_confirm = (ButtonIcon) getActivity().findViewById(
                R.id.tool_confirm_bticn);
        content_et.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        tool_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                 * 关闭输入弹窗
				 */
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity()
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                content = content_et.getText().toString();
                if (!content.trim().equals("")) {
                    Comments c = new Comments();
                    parentAct = getActivity();
                    String tempid = "";
                    if (parentAct instanceof GoodsDetailsActivity) {
                        action = Constants.ACTION_GOODSDETAIL;
                        sellerName = ((GoodsDetailsActivity) parentAct).getSellerName();
                    }
                    if (parentAct instanceof WantedGoodsDetailsActivity) {
                        action = Constants.ACTION_WGOODSDETAIL;
                        answerTo = ((WantedGoodsDetailsActivity) parentAct)
                                .getAnswerTo();
                        sellerName = ((WantedGoodsDetailsActivity) parentAct).getSellerName();
                    }
                    content_et.setText("");
                    c.setGoodsID(goodsId);
                    c.setUserId(localuser.getObjectId());
                    c.setCommets(content);
                    c.setUserAvator(localuser.getAvator());
                    c.setUsername(localuser.getUsername());
                    if (answerTo != null) {
                        c.setAnswerto(answerTo);
                    }
                    c.save(getActivity(), new SaveListener() {

                        @Override
                        public void onSuccess() {
                            Toast.makeText(getActivity(), "评论成功(●'◡'●)", Toast.LENGTH_SHORT)
                                    .show();
                            if (answerTo != null&&!answerTo.equals(localuser.getUsername())) {
                                pushToHim(answerTo, localuser.getUsername()
                                        + "回复了你:"+content, action, goodsId);
                            }
                           if (!sellerName.equals(localuser.getUsername())&&!sellerName.equals(answerTo)){
                               pushToHim(sellerName, localuser.getUsername()
                                       + "评论了你的商品", action, goodsId);
                           }
                            getFragmentManager().popBackStack();// 返回上一个fragment
                            if (parentAct instanceof GoodsDetailsActivity) {
                                ((GoodsDetailsActivity) parentAct)
                                        .reflashComments(true);
                            }
                            if (parentAct instanceof WantedGoodsDetailsActivity) {
                                ((WantedGoodsDetailsActivity) parentAct)
                                        .reflashComments(true);
                            }
                        }

                        private void pushToHim(String name, String msg, final int action, final String goodsId) {
                            BmobQuery<MyUser> bq = new BmobQuery<MyUser>();
                            bq.addWhereEqualTo("username", name);
                            final String text = msg;
                            bq.findObjects(getActivity(),
                                    new FindListener<MyUser>() {

                                        @Override
                                        public void onSuccess(List<MyUser> arg0) {
                                            if (arg0.size() > 0) {
                                                String installationId = arg0
                                                        .get(0)
                                                        .getInstallationID();
                                                if (installationId != null) {
                                                    BmobPushManager bmobPush = new BmobPushManager(
                                                            parentAct);
                                                    BmobQuery<BmobInstallation> query = BmobInstallation
                                                            .getQuery();
                                                    query.addWhereEqualTo(
                                                            "installationId",
                                                            installationId);
                                                    bmobPush.setQuery(query);
                                                    JSONObject jsonObject = new JSONObject();
                                                    try {
                                                        jsonObject.put("alert", text);
                                                        jsonObject.put("action", action);
                                                        jsonObject.put("goodsId", goodsId);
                                                        bmobPush.pushMessage(jsonObject);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(int arg0,
                                                            String arg1) {

                                        }
                                    });

                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
                            // ShowToast("评论失败( ▼-▼ ");
                            Toast.makeText(getActivity(), "评论失败( ▼-▼", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }
        });

    }

    public void initListeners() {
        // TODO Auto-generated method stub

    }

    public void initData() {
        // TODO Auto-generated method stub

    }

    /**
     * 设置输入框提示
     *
     * @param hint
     */
    public void setHint(String hint) {
        if (content_et != null) {
            content_et.setHint(hint);
        }
    }

}
