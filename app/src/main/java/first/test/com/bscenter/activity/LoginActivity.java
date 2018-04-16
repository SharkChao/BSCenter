package first.test.com.bscenter.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.IdentityListener;
import com.iflytek.cloud.IdentityResult;
import com.iflytek.cloud.IdentityVerifier;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.kongqw.util.FaceUtil;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import first.test.com.bscenter.BuildConfig;
import first.test.com.bscenter.R;
import first.test.com.bscenter.activity.main.MainActivity;
import first.test.com.bscenter.activity.main.WelcomActivity;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.base.MyLeanCloudApp;
import first.test.com.bscenter.constants.Constants;
import first.test.com.bscenter.core.common.FileType;
import first.test.com.bscenter.core.engine.ResourceManager;
import first.test.com.bscenter.dao.DaoFactory;
import first.test.com.bscenter.dao.impl.FavoriteDao;
import first.test.com.bscenter.dao.impl.FileAppNameDao;
import first.test.com.bscenter.dao.impl.FileTypeDao;
import first.test.com.bscenter.databinding.ActivityLoginBinding;
import first.test.com.bscenter.event.FaceClearEvent;
import first.test.com.bscenter.event.FaceSetEvent;
import first.test.com.bscenter.event.SSClearEvent;
import first.test.com.bscenter.event.SSSetEvent;
import first.test.com.bscenter.event.VoiceClearEvent;
import first.test.com.bscenter.event.VoiceSetEvent;
import first.test.com.bscenter.greendao.DaoMaster;
import first.test.com.bscenter.greendao.DaoSession;
import first.test.com.bscenter.greendao.GreenDaoManager;
import first.test.com.bscenter.greendao.UserDao;
import first.test.com.bscenter.model.Favorite;
import first.test.com.bscenter.model.User;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.CommonUtil;
import first.test.com.bscenter.utils.PasswordUtil;
import first.test.com.bscenter.utils.PermisionUtils;
import first.test.com.bscenter.views.FingerPrinterView;
import first.test.com.bscenter.views.PhotoPopupWindow;
import io.reactivex.observers.DisposableObserver;
import zwh.com.lib.FPerException;
import zwh.com.lib.RxFingerPrinter;


/**
 * Created by Admin on 2018/4/11.
 */
@ContentView(R.layout.activity_login)

public class LoginActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi {
    private EditText etName;
    private EditText etPassword;
    private TextView btnRegister;
    private Button btnLogin;
    private TextView btnMore;
    private ActivityLoginBinding mBinding;
    private Button mRvSetting;
    private Button mRvSettingFace;
//    private PermissionsManager mPermissionsManager;

    public static Map<String, int[]> mExtensionTypeMap = null;
    public static Map<String, String> mAppNameMap = null;
    private static final String TAG = "DeploymentOperation";
    private static final String DATABASE_NAME = "appdirname.db";
    private Button mRvSettingVoice;
    private IdentityVerifier mIdVerifier;

    @Override
    public void initTitle() {
        isShowToolBar(false);
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {
        mBinding = (ActivityLoginBinding) viewDataBinding;
        btnRegister = mBinding.btnRegister;
        btnLogin = mBinding.btnLogin;
        etName = mBinding.edtAccount;
        etPassword = mBinding.edtPassword;
        btnMore = mBinding.btnMore;
    }

    @Override
    public void initData() {
        requestPermissions();
        EventBus.getDefault().register(this);
        deployeDataBase(getApplicationContext(), false);


//        initResource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void requestPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},0x0010);
                }

                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void initialFavoriteDatabase() {
//        PermisionUtils.verifyStoragePermissions(BaseActivity.currentActivity);
        File file = new File("/");
        int size = 0;
        if (file != null && file.listFiles() != null) {
            size = file.listFiles().length;
        }
        Favorite favoriteRoot = new Favorite("/", "Root目录", "根目录", FileType.TYPE_FOLDER, System.currentTimeMillis(), size, "安装时加入");
        FavoriteDao dao = DaoFactory.getFavoriteDao(MyLeanCloudApp.getInstance());
//        dao.insertFavorite(favoriteRoot);

        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            file = Environment.getExternalStorageDirectory();
            if (file != null && file.list() != null) {
                size = file.list().length;
            }
            try {
                Favorite favoriteStorage = new Favorite(file.getCanonicalPath(), "存储卡", "存储卡", FileType.TYPE_FOLDER, System.currentTimeMillis(),
                        size, "外部存储卡");
//                dao.insertFavorite(favoriteStorage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 强制要求重新加载
     *
     * @param context
     * @param isForced
     */
    private void deployeDataBase(Context context, boolean isForced) {
        PermisionUtils.verifyStoragePermissions(this);
        File dir = new File(ResourceManager.DATABASES_DIR);
        if (!dir.exists() || isForced) {
            try {
                dir.mkdir();
            } catch (Exception e) {
                Log.e(TAG, "--->创建数据库目录失败");
                e.printStackTrace();
            }
        }

        File dest = new File(dir, DATABASE_NAME);
        if (dest.exists() && !isForced) {
            return;
        }

        FileOutputStream out = null;
        InputStream in = null;

        try {
            if (dest.exists()) {
                dest.delete();
            }

            dest.createNewFile();
            in = context.getAssets().open(DATABASE_NAME, Context.MODE_PRIVATE);
            int size = in.available();
            byte buf[] = new byte[size];
            in.read(buf);
            out = new FileOutputStream(dest);
            out.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "--->拷贝数据库成功！");

        initialFavoriteDatabase();
    }

    // 加载文件路径对应的应用名
    private void loadAppNameMap() {
        if (mAppNameMap != null) {
            mAppNameMap.clear();
        }
        FileAppNameDao dao = DaoFactory.getFileAppNameDao(getApplicationContext());
        mAppNameMap = dao.findAllAppName();
    }

    private void initResource() {
        loadExtesionTypeMap();
        loadAppNameMap();
        loadResourceManager(getApplicationContext());
    }

    private void loadResourceManager(Context applicationContext) {

    }

    /**
     * 加载扩展名对应文件数据类型
     */
    private void loadExtesionTypeMap() {
        if (mExtensionTypeMap != null) {
            mExtensionTypeMap.clear();
        }
        FileTypeDao dao = DaoFactory.getFileTypeDao(getApplicationContext());
        mExtensionTypeMap = dao.getAllExtensionFileTypeMap();
        // showMap(mExtensionTypeMap);
    }

    private void showMap(Map<String, int[]> map) {
        for (String key : map.keySet()) {
            System.out.println("扩展名：" + key + " 类型： " + map.get(key)[0] + ", 类别：" + map.get(key)[1]);
        }
    }

    @Override
    protected void initEvent() {

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtil.isStrEmpty(etName.getText().toString())) {
                    CommonUtil.showSnackBar(btnRegister, "请先输入账号");
                    return;
                }
                if (CommonUtil.isStrEmpty(etPassword.getText().toString())) {
                    CommonUtil.showSnackBar(btnRegister, "请先输入密码");
                    return;
                }

                User user = new User();
                user.setName(etName.getText().toString());
                user.setPassword(etPassword.getText().toString());

                DaoMaster master = GreenDaoManager.getInstance(LoginActivity.this)
                        .getMaster();
                DaoSession daoSession = master.newSession();
                UserDao userDao = daoSession.getUserDao();
                User unique = userDao.queryBuilder().where(UserDao.Properties.Name.eq(user.getName()), UserDao.Properties.Password.eq(user.getPassword())).unique();
                if (unique == null) {
                    Toast.makeText(LoginActivity.this, "登录失败,请核对账号密码后重试!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PhotoPopupWindow popupWindow = new PhotoPopupWindow(LoginActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlert1();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlert2();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlert3();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlert4();
                    }
                });

                View rootView = LayoutInflater.from(LoginActivity.this)
                        .inflate(R.layout.activity_login, null);
                popupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }

    public void showAlert1() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view1, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        final FingerPrinterView fingerPrinterView = (FingerPrinterView) view.findViewById(R.id.fpv);
        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override
            public void onChange(int state) {
                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                    Toast.makeText(LoginActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (state == FingerPrinterView.STATE_WRONG_PWD) {
                    Toast.makeText(LoginActivity.this, "指纹识别失败，请重试",
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
                if (e instanceof FPerException) {
                    Toast.makeText(LoginActivity.this, ((FPerException) e).getDisplayMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                } else {
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                }
            }
        };
        rxfingerPrinter.dispose();
        rxfingerPrinter.begin().subscribe(observer);
        rxfingerPrinter.addDispose(observer);

        alertDialog.show();
    }

    public void showAlert2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_view2, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        mRvSetting = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);

        String pin = PasswordUtil.getPin(this);
        mRvSetting.setVisibility(CommonUtil.isStrEmpty(pin) ? View.VISIBLE : View.GONE);
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
        Intent intent = new Intent(LoginActivity.this, GestureDetailActivity.class);
        intent.putExtra(Constants.INTENT_SECONDACTIVITY_KEY, mode);
        intent.putExtra(Constants.INTENT_type_KEY, 0);
        startActivity(intent);
    }

    private void showAlert3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_view3, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        mRvSettingFace = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);
        Bitmap bitmap = FaceUtil.getImage(this, Constants.FACE_FILE_NAME_SAVE);
        mRvSettingFace.setVisibility(CommonUtil.isNotEmpty(bitmap) ? View.GONE : View.VISIBLE);
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

    private void actionFaceActivity(int value) {
        // 要校验的权限
        PermisionUtils.verifyCameraPermissions(this);
        if (value != Constants.FACE_MODE_SET) {
            Bitmap bitmap = FaceUtil.getImage(this, Constants.FACE_FILE_NAME_SAVE);
            if (!CommonUtil.isNotEmpty(bitmap)) {
                Toast.makeText(getBaseContext(), "请先设置人脸特征!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(LoginActivity.this, FaceDetailActivity.class);
        intent.putExtra("face_key", value);
        intent.putExtra(Constants.INTENT_type_KEY, 0);
        startActivity(intent);

    }

    private void actionVoiceActivity(int value) {
        // 要校验的权限
//        PermisionUtils.verifyCameraPermissions(this);

        Intent intent = new Intent(LoginActivity.this, VocalVerifyActivity.class);
        intent.putExtra("voice_key", value);
        startActivity(intent);

    }

    private void showAlert4() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_view3, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        mRvSettingVoice = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);
        mIdVerifier = IdentityVerifier.createVerifier(LoginActivity.this, new InitListener() {

            @Override
            public void onInit(int errorCode) {
                if (ErrorCode.SUCCESS == errorCode) {
                    Toast.makeText(LoginActivity.this, "引擎初始化成功!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "引擎初始化失败，错误码："+errorCode, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置声纹模型参数
        // 清空参数
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ivp");
        // 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, "1");

        // 子业务执行参数，若无可以传空字符传
        StringBuffer params3 = new StringBuffer();
        // 设置模型操作的密码类型
        int mPwdType = 3;
        params3.append("pwdt=" + mPwdType + ",");
        // 执行模型操作
        mIdVerifier.execute("ivp", "query", params3.toString(), mModelListener);

//        mRvSettingVoice.setVisibility(CommonUtil.isNotEmpty(bitmap) ? View.GONE : View.VISIBLE);
        rvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionVoiceActivity(Constants.VOICE_MODE_CLEAR);
            }
        });
        rvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                actionFaceActivity(Constants.FACE_MODE_EDIT);
            }
        });
        mRvSettingVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionVoiceActivity(Constants.VOICE_MODE_SET);
//                Intent intent = new Intent(LoginActivity.this,VocalVerifyActivity.class);
//                startActivity(intent);
            }
        });
        rvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionVoiceActivity(Constants.VOICE_MODE_VERFIY);
            }
        });

        builder.setView(contentView);
        builder.create().show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSSetEvent event) {
        if (event != null) {
            if (mRvSetting != null) {
                mRvSetting.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSClearEvent event) {
        if (event != null) {
            if (mRvSetting != null) {
                mRvSetting.setVisibility(View.VISIBLE);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VoiceClearEvent event) {
        if (event != null) {
            if (mRvSettingVoice != null) {
                mRvSettingVoice.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceSetEvent event) {
        if (event != null) {
            if (mRvSettingFace != null) {
                mRvSettingFace.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VoiceSetEvent event) {
        if (event != null) {
            if (mRvSettingVoice != null) {
                mRvSettingVoice.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceClearEvent event) {
        if (event != null) {
            if (mRvSettingFace != null) {
                mRvSettingFace.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 声纹模型操作监听器
     */
    private IdentityListener mModelListener = new IdentityListener() {

        @Override
        public void onResult(IdentityResult result, boolean islast) {
            Log.d(TAG, "model operation:" + result.getResultString());


            JSONObject jsonResult = null;
            int ret = ErrorCode.SUCCESS;
            try {
                jsonResult = new JSONObject(result.getResultString());
                ret = jsonResult.getInt("ret");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (ErrorCode.SUCCESS == ret) {
                if (mRvSettingVoice != null){
                    mRvSettingVoice.setVisibility(View.GONE);
                }
            } else {
//                Toast.makeText(LoginActivity.this, "模型不存在", Toast.LENGTH_SHORT).show();
                if (mRvSettingVoice != null){
                    mRvSettingVoice.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onError(SpeechError error) {
            Toast.makeText(LoginActivity.this, error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
        }
    };
}
