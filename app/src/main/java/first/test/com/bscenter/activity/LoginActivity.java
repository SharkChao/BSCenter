package first.test.com.bscenter.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.kongqw.util.FaceUtil;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.StringUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.constants.Constants;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.CommonUtil;
import first.test.com.bscenter.utils.PasswordUtil;
import first.test.com.bscenter.views.FingerPrinterView;
import first.test.com.bscenter.views.PhotoPopupWindow;
import io.reactivex.observers.DisposableObserver;
import zwh.com.lib.FPerException;
import zwh.com.lib.RxFingerPrinter;

/**
 * Created by Admin on 2018/4/11.
 */
@Route(path = "/center/LoginActivity")
@ContentView(R.layout.activity_login)

public class LoginActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi{
    private EditText etName;
    private EditText etPassword;
    private TextView btnRegister;
    private Button btnLogin;
    private TextView btnMore;
    private ActivityLoginBinding mBinding;

    @Override
    public void initTitle() {
        isShowToolBar(false);
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {
        mBinding = (ActivityLoginBinding) viewDataBinding;
        btnRegister = mBinding.btnRegister;
        btnLogin  = mBinding.btnLogin;
        etName = mBinding.edtAccount;
        etPassword = mBinding.edtPassword;
        btnMore = mBinding.btnMore;
    }

    @Override
    public void initData() {

    }

    @Override
    protected void initEvent() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance()
                        .build("/center/RegisterActivity")
                        .navigation();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtil.isStrEmpty(etName.getText().toString())){
                    CommonUtil.showSnackBar(btnRegister,"请先输入账号");
                    return;
                }
                if (CommonUtil.isStrEmpty(etPassword.getText().toString())){
                    CommonUtil.showSnackBar(btnRegister,"请先输入密码");
                    return;
                }
                BmobUser user = new BmobUser();
                user.setUsername(etName.getText().toString());
                user.setPassword(etPassword.getText().toString());
                user.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e == null){
                            Toast.makeText(LoginActivity.this, "登陆成功!", Toast.LENGTH_SHORT).show();
                            ARouter.getInstance().build("/center/HomeActivity")
                                    .navigation();
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "登录失败!"+e.getErrorCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                View rootView = LayoutInflater.from(LoginActivity.this)
                        .inflate(R.layout.activity_login, null);
                popupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }

    public void showAlert1(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alert_view1,null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        final FingerPrinterView fingerPrinterView = (FingerPrinterView)view.findViewById(R.id.fpv);
        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override public void onChange(int state) {
                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                    Toast.makeText(LoginActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    ARouter.getInstance()
                            .build("/center/HomeActivity")
                            .navigation();
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
                if(e instanceof FPerException){
                    Toast.makeText(LoginActivity.this,((FPerException) e).getDisplayMessage(),Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_view2, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        Button rvSetting = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);

        String pin = PasswordUtil.getPin(this);
        rvSetting.setVisibility(CommonUtil.isStrEmpty(pin)?View.VISIBLE:View.GONE);
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
        rvSetting.setOnClickListener(new View.OnClickListener() {
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
        startActivity(intent);
    }

    private void showAlert3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_view2, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        Button rvSetting = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);

        Bitmap bitmap = FaceUtil.getImage(this, Constants.FACE_FILE_NAME_SAVE);
        rvSetting.setVisibility(CommonUtil.isNotEmpty(bitmap)?View.GONE:View.VISIBLE);
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
        rvSetting.setOnClickListener(new View.OnClickListener() {
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
        ARouter.getInstance()
                .build("/center/FaceDetailActivity")
                .withInt("face_key",value)
                .navigation();
    }
}