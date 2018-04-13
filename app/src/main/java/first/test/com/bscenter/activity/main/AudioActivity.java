package first.test.com.bscenter.activity.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
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
import first.test.com.bscenter.adapter.AudioListViewAdapter;
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
import first.test.com.bscenter.model.Audio;
import first.test.com.bscenter.model.Favorite;
import first.test.com.bscenter.utils.CommonUtil;
import first.test.com.bscenter.utils.DensityUtil;
import first.test.com.bscenter.utils.FileUtils;
import first.test.com.bscenter.utils.OpenFileUtil;
import first.test.com.bscenter.utils.PasswordUtil;
import first.test.com.bscenter.utils.UiUtil;
import first.test.com.bscenter.views.AudioInfoDialog;
import first.test.com.bscenter.views.FingerPrinterView;
import first.test.com.bscenter.views.PhotoPopupWindow;
import io.reactivex.observers.DisposableObserver;
import zwh.com.lib.FPerException;
import zwh.com.lib.RxFingerPrinter;


public class AudioActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

    protected static final int MSG_UPDATE_DATA = 0x1031;

    protected static final int MSG_PRE_LOAD = 0x1032;

    protected static final int MSG_FINISH = 0x1033;

    private List<Audio> mAudios = null;

    private ListView mListView = null;
    private AudioListViewAdapter mAdapter = null;
    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private View mVideoPopInfo = null;
    private View mVideoPopDelete = null;
    private View mVideoPopShare = null;
    private View mVideoPopFavorite = null;
    private View mViewNothing = null;
    private int mChoosePosition = 0;
    private Dialog mProgressDialog = null;

    private AudioLoadThread mThread = null;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == MSG_PRE_LOAD) {
                mProgressDialog.show();
            } else if (msg.what == MSG_UPDATE_DATA) {
                mAudios.add((Audio) msg.obj);
                if (mAudios.size() % 15 == 0) {
                    mAdapter.notifyDataSetChanged();
                }
            } else if (msg.what == MSG_FINISH) {
                Collections.sort(mAudios, new Comparator<Audio>() {
                    @Override
                    public int compare(Audio lhs, Audio rhs) {
                        return lhs.getPath().compareTo(rhs.getPath());
                    }
                });
                mProgressDialog.dismiss();
                mAdapter.notifyDataSetChanged();
                if (mAudios.size() == 0) {
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
        mAudios = new ArrayList<Audio>();
        setContentView(R.layout.activity_audio);
        EventBus.getDefault().register(this);
        mProgressDialog = UiUtil.createLoadingDialog(this, "正在为您加载音乐...");
        mViewNothing = findViewById(R.id.nothing);

        mListView = (ListView) findViewById(R.id.mAudioListView);
        mAdapter = new AudioListViewAdapter(this, mListView, mAudios);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mThread = new AudioLoadThread();
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
        Intent intent = OpenFileUtil.openFile(mAudios.get(position).getPath());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mChoosePosition = position;
        showWindow(view, position);
        return true;
    }

    @SuppressWarnings("deprecation")
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

        mVideoPopFavorite.setOnClickListener(this);
        mVideoPopInfo.setOnClickListener(this);
        mVideoPopDelete.setOnClickListener(this);
        mVideoPopShare.setOnClickListener(this);

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + DensityUtil.getWindowSize()[0] / 3, location[1]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mVideoPopInfo:
            mPopupWindow.dismiss();
            AudioInfoDialog mInfoDialog = new AudioInfoDialog(this, mAudios.get(mChoosePosition));
            mInfoDialog.show();
            break;

        case R.id.mVideoPopDelete:
            mPopupWindow.dismiss();
            String fileName = mAudios.get(mChoosePosition).getTilte();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒！").setMessage("你确定要删除 " + fileName + "吗？");
            dialog.setNegativeButton("取消", null);
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(mAudios.get(mChoosePosition).getPath());
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(AudioActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mAudios.remove(mChoosePosition);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AudioActivity.this, "很遗憾，没有帮您完成任务~~", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            break;

        case R.id.mVideoPopShare:
            mPopupWindow.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            String filePath = mAudios.get(mChoosePosition).getPath();
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "音乐分享");
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

    class AudioLoadThread extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_PRE_LOAD);

            Cursor c = null;
            try {
                FileCategoryPageFragment.mAllAudioSize = 0;
                c = AudioActivity.this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

                while (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                    if (!FileUtils.isExists(path)) {
                        continue;
                    }

                    int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 时间
                    String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                    int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时间
                    long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                    String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                    String tilte = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                    int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    FileCategoryPageFragment.mAllAudioSize += size;

                    Audio audio = new Audio(id, path, tilte, artist, album, albumId, duration, size);

                    Message msg = new Message();
                    msg.what = MSG_UPDATE_DATA;
                    msg.obj = audio;
                    mHandler.sendMessage(msg);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mHandler.sendEmptyMessage(MSG_FINISH);
                if (c != null) {
                    c.close();
                }
            }

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
                    Toast.makeText(AudioActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    setAction();
                }
                if (state == FingerPrinterView.STATE_WRONG_PWD) {
                    Toast.makeText(AudioActivity.this, "指纹识别失败，请重试",
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
                    Toast.makeText(AudioActivity.this,((FPerException) e).getDisplayMessage(),Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AudioActivity.this);
        View contentView = LayoutInflater.from(AudioActivity.this).inflate(R.layout.dialog_view2, null);

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
        Intent intent = new Intent(AudioActivity.this, GestureDetailActivity.class);
        intent.putExtra(Constants.INTENT_SECONDACTIVITY_KEY, mode);
        intent.putExtra(Constants.INTENT_type_KEY, 1);
        startActivity(intent);
    }

    private void showAlert3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AudioActivity.this);
        View contentView = LayoutInflater.from(AudioActivity.this).inflate(R.layout.dialog_view3, null);

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
        Intent intent = new Intent(AudioActivity.this,FaceDetailActivity.class);
        intent.putExtra("face_key",value);
        intent.putExtra(Constants.INTENT_type_KEY, 1);
        startActivity(intent);
    }

    public void showDialog(){
        mPopupWindow1 = new PhotoPopupWindow(AudioActivity.this, new OnClickListener() {
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

        View rootView = LayoutInflater.from(AudioActivity.this)
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
        String path = mAudios.get(mChoosePosition).getPath();
        if (null != dao.findFavoriteByFullPath(path)) {
            Toast.makeText(this, "已经在保险箱中了,你一定特别喜欢这首歌", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(path);
        Favorite favorite = new Favorite(path, file.getName(), "", FileType.TYPE_MP3, System.currentTimeMillis(), file.length(), "");
        dao.insertFavorite(favorite);
        Toast.makeText(this, "成功添加到保险箱！", Toast.LENGTH_SHORT).show();

        mAudios.remove(mChoosePosition);
        mAdapter.notifyDataSetChanged();
        mPopupWindow1.dismiss();

        new EventBus().postSticky(new BXXClearEvent());
    }
}
