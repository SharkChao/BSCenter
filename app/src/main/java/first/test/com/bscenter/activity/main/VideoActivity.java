package first.test.com.bscenter.activity.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.kongqw.util.FaceUtil;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import first.test.com.bscenter.BuildConfig;
import first.test.com.bscenter.R;
import first.test.com.bscenter.activity.FaceDetailActivity;
import first.test.com.bscenter.activity.GestureDetailActivity;
import first.test.com.bscenter.adapter.VideoGridViewAdapter;
import first.test.com.bscenter.constants.Constants;
import first.test.com.bscenter.core.common.FileType;
import first.test.com.bscenter.dao.DaoFactory;
import first.test.com.bscenter.dao.impl.FavoriteDao;
import first.test.com.bscenter.event.BXXClearEvent;
import first.test.com.bscenter.event.FaceClearEvent;
import first.test.com.bscenter.event.FaceSetEvent;
import first.test.com.bscenter.event.FaceVerifyEvent;
import first.test.com.bscenter.event.SSClearEvent;
import first.test.com.bscenter.event.SSSetEvent;
import first.test.com.bscenter.event.SSVerifyEvent;
import first.test.com.bscenter.fragment.FileCategoryPageFragment;
import first.test.com.bscenter.model.Favorite;
import first.test.com.bscenter.model.Video;
import first.test.com.bscenter.utils.CommonUtil;
import first.test.com.bscenter.utils.DensityUtil;
import first.test.com.bscenter.utils.OpenFileUtil;
import first.test.com.bscenter.utils.PasswordUtil;
import first.test.com.bscenter.utils.UiUtil;
import first.test.com.bscenter.views.FingerPrinterView;
import first.test.com.bscenter.views.PhotoPopupWindow;
import first.test.com.bscenter.views.VideoInfoDialog;
import io.reactivex.observers.DisposableObserver;
import zwh.com.lib.FPerException;
import zwh.com.lib.RxFingerPrinter;


public class VideoActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

    protected static final int MSG_PRE_LOAD = 0x1020;

    protected static final int MSG_UPDATE_DATA = 0x1021;

    protected static final int MSG_FINSH_LOAD = 0x1023;

    private List<Video> mVideos = new ArrayList<Video>();

    private GridView mGridView = null;
    private VideoGridViewAdapter mAdapter = null;
    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private View mVideoPopInfo = null;
    private View mVideoPopDelete = null;
    private View mVideoPopShare = null;
    private View mVideoPopFavorite = null;
    private View mViewNothing = null;
    private int mChoosePosition = 0;

    private Dialog mDialog = null;

    private VideoLoadThread mThread = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == MSG_PRE_LOAD) {
                mDialog = UiUtil.createLoadingDialog(VideoActivity.this, "视频还真不少呢，看我玩命加载...");
                mDialog.show();
            } else if (msg.what == MSG_UPDATE_DATA) {
                mVideos.add((Video) msg.obj);
                if (mVideos.size() % 10 == 0) {
                    mAdapter.notifyDataSetChanged();
                }
            } else if (msg.what == MSG_FINSH_LOAD) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                mAdapter.notifyDataSetChanged();
                if (mVideos.size() == 0) {
                    mViewNothing.setVisibility(View.VISIBLE);
                } else {
                    mViewNothing.setVisibility(View.GONE);
                }
            }
        };
    };
    private PhotoPopupWindow mPopupWindow1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        EventBus.getDefault().register(this);
        mViewNothing = findViewById(R.id.nothing);

        mGridView = (GridView) findViewById(R.id.mVideoGrideView);
        mAdapter = new VideoGridViewAdapter(this, mVideos, mGridView);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        mThread = new VideoLoadThread();
        mThread.start();
    }

    public void back(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path = mVideos.get(position).getPath();
        File file = new File(path);
        if (file.exists()) {
            Intent intent = OpenFileUtil.openFile(path);
            startActivity(intent);
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mChoosePosition = position;
        showWindow(view, position);
        return true;
    }

    private void showWindow(View parent, int position) {

        if (mPopupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPopView = layoutInflater.inflate(R.layout.pop_video_op, null);
            mPopupWindow = new PopupWindow(mPopView, DensityUtil.dip2px(this, 180), DensityUtil.dip2px(this, 45));
        }
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);

        mVideoPopInfo = mPopView.findViewById(R.id.mVideoPopInfo);
        mVideoPopDelete = mPopView.findViewById(R.id.mVideoPopDelete);
        mVideoPopShare = mPopView.findViewById(R.id.mVideoPopShare);
        mVideoPopFavorite = mPopView.findViewById(R.id.mVideoPopFavorite);

        mVideoPopInfo.setOnClickListener(this);
        mVideoPopDelete.setOnClickListener(this);
        mVideoPopShare.setOnClickListener(this);
        mVideoPopFavorite.setOnClickListener(this);

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] + DensityUtil.dip2px(this, 10));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mVideoPopInfo:
            mPopupWindow.dismiss();
            VideoInfoDialog infoDialog = new VideoInfoDialog(this, mVideos.get(mChoosePosition));
            infoDialog.show();
            break;

        case R.id.mVideoPopDelete:
            mPopupWindow.dismiss();
            String fileName = mVideos.get(mChoosePosition).getName();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒！").setMessage("你确定要删除 " + fileName + "吗？");
            dialog.setNegativeButton("取消", null);
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(mVideos.get(mChoosePosition).getPath());
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(VideoActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mVideos.remove(mChoosePosition);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(VideoActivity.this, "很遗憾，没有帮您完成任务~~", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            break;

        case R.id.mVideoPopShare:
            mPopupWindow.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            String filePath = mVideos.get(mChoosePosition).getPath();
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "视频分享");
            intent.putExtra(Intent.EXTRA_TEXT, "我想分享给你好心情！");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
            break;

        case R.id.mVideoPopFavorite:
            mPopupWindow.dismiss();
           showDialog();
            break;
        default:
            break;
        }
    }

    class VideoLoadThread extends Thread {

        @Override
        public void run() {
            List<Video> videos = FileCategoryPageFragment.getVideos();
            if (videos.size() > 20) {
                mHandler.sendEmptyMessage(MSG_PRE_LOAD);
            }
            FileCategoryPageFragment.mAllVideoSize = 0;
            for (int i = 0; i < videos.size(); i++) {
                Message msg = new Message();
                msg.what = MSG_UPDATE_DATA;
                msg.obj = videos.get(i);
                mHandler.sendMessage(msg);
            }
            mHandler.sendEmptyMessage(MSG_FINSH_LOAD);

        }

    }


    private Button mRvSetting;
    private Button mRvSettingFace;
    public void showAlert1(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view1,null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        final FingerPrinterView fingerPrinterView = (FingerPrinterView)view.findViewById(R.id.fpv);
        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override public void onChange(int state) {
                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                    Toast.makeText(VideoActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    setAction();
                }
                if (state == FingerPrinterView.STATE_WRONG_PWD) {
                    Toast.makeText(VideoActivity.this, "指纹识别失败，请重试",
                            Toast.LENGTH_SHORT).show();
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                }
            }
        });
        RxFingerPrinter rxfingerPrinter = new RxFingerPrinter(this);
        rxfingerPrinter.setLogging(BuildConfig.DEBUG);


        DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {

            @Override
            protected void onStart() {
                if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
                    return;
                } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
                        || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                } else {
                    fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);
                }
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof FPerException){
                    Toast.makeText(VideoActivity.this,((FPerException) e).getDisplayMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                }else{
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                }
            }
        };
        rxfingerPrinter.dispose();
        rxfingerPrinter.begin().subscribe(observer);
        rxfingerPrinter.addDispose(observer);

        alertDialog.show();
    }

    public void showAlert2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
        View contentView = LayoutInflater.from(VideoActivity.this).inflate(R.layout.dialog_view2, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        mRvSetting = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);

        String pin = PasswordUtil.getPin(this);
        mRvSetting.setVisibility(CommonUtil.isStrEmpty(pin)?View.VISIBLE:View.GONE);
        rvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSecondActivity(LockMode.CLEAR_PASSWORD);
            }
        });
        rvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSecondActivity(LockMode.EDIT_PASSWORD);
            }
        });
        mRvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSecondActivity(LockMode.SETTING_PASSWORD);
            }
        });
        rvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSecondActivity(LockMode.VERIFY_PASSWORD);
            }
        });

        builder.setView(contentView);
        builder.create().show();
    }
    /**
     * 跳转到密码处理界面
     */
    private void actionSecondActivity(LockMode mode) {
        if (mode != LockMode.SETTING_PASSWORD) {
            if (StringUtils.isEmpty(PasswordUtil.getPin(this))) {
                Toast.makeText(getBaseContext(), "请先设置密码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(VideoActivity.this, GestureDetailActivity.class);
        intent.putExtra(Constants.INTENT_SECONDACTIVITY_KEY, mode);
        intent.putExtra(Constants.INTENT_type_KEY, 1);
        startActivity(intent);
    }

    private void showAlert3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
        View contentView = LayoutInflater.from(VideoActivity.this).inflate(R.layout.dialog_view3, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        mRvSettingFace = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);
        Bitmap bitmap = FaceUtil.getImage(this, Constants.FACE_FILE_NAME_SAVE);
        mRvSettingFace.setVisibility(CommonUtil.isNotEmpty(bitmap)?View.GONE:View.VISIBLE);
        rvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionFaceActivity(Constants.FACE_MODE_CLEAR);
            }
        });
        rvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionFaceActivity(Constants.FACE_MODE_EDIT);
            }
        });
        mRvSettingFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionFaceActivity(Constants.FACE_MODE_SET);
            }
        });
        rvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionFaceActivity(Constants.FACE_MODE_VERFIY);
            }
        });

        builder.setView(contentView);
        builder.create().show();
    }

    private void actionFaceActivity(int value){
        // 要校验的权限
        if (value != Constants.FACE_MODE_SET) {
            Bitmap bitmap = FaceUtil.getImage(this, Constants.FACE_FILE_NAME_SAVE);
            if (!CommonUtil.isNotEmpty(bitmap)) {
                Toast.makeText(getBaseContext(), "请先设置人脸特征!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(VideoActivity.this,FaceDetailActivity.class);
        intent.putExtra("face_key",value);
        intent.putExtra(Constants.INTENT_type_KEY, 1);
        startActivity(intent);
    }

    public void showDialog(){
        mPopupWindow1 =
                new PhotoPopupWindow(VideoActivity.this, new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert1();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert2();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert3();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        View rootView = LayoutInflater.from(VideoActivity.this)
                .inflate(R.layout.activity_login, null);
        mPopupWindow1.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSSetEvent event) {
        if (event != null){
            if (mRvSetting  != null){
                mRvSetting.setVisibility(View.GONE);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSVerifyEvent event) {
        if (event != null){
//            if (mRvSetting  != null){
//                mRvSetting.setVisibility(View.VISIBLE);
//            }
            setAction();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSClearEvent event) {
        if (event != null){
            if (mRvSetting  != null){
                mRvSetting.setVisibility(View.VISIBLE);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceSetEvent event) {
        if (event != null){
            if (mRvSettingFace  != null){
                mRvSettingFace.setVisibility(View.GONE);
            }
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceVerifyEvent event) {
        if (event != null){
//            if (mRvSetting  != null){
//                mRvSetting.setVisibility(View.VISIBLE);
//            }
            setAction();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceClearEvent event) {
        if (event != null){
            if (mRvSettingFace  != null){
                mRvSettingFace.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setAction(){
        FavoriteDao dao = DaoFactory.getFavoriteDao(this);
        String path = mVideos.get(mChoosePosition).getPath();
        if (null != dao.findFavoriteByFullPath(path)) {
            Toast.makeText(this, "已经在保险箱中了,你一定特别喜欢这个视频", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(path);
        Favorite favorite = new Favorite(path, file.getName(), "", FileType.TYPE_MP4, System.currentTimeMillis(), file.length(), "");
        dao.insertFavorite(favorite);
        Toast.makeText(this, "成功添加到保险箱！", Toast.LENGTH_SHORT).show();
        mVideos.remove(mChoosePosition);
        mAdapter.notifyDataSetChanged();
        mPopupWindow1.dismiss();

        new EventBus().postSticky(new BXXClearEvent());
    }

}
