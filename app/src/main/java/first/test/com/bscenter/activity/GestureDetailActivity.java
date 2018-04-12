package first.test.com.bscenter.activity;

import android.databinding.ViewDataBinding;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.kongqw.interfaces.OnFaceDetectorListener;
import com.kongqw.interfaces.OnOpenCVInitListener;
import com.kongqw.view.CameraFaceDetectionView;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.view.CustomLockView;


import org.greenrobot.eventbus.EventBus;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.constants.Constants;
import first.test.com.bscenter.event.SSClearEvent;
import first.test.com.bscenter.event.SSSetEvent;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.PasswordUtil;

import static com.leo.gesturelibray.enums.LockMode.EDIT_PASSWORD;
import static com.leo.gesturelibray.enums.LockMode.SETTING_PASSWORD;
import static com.leo.gesturelibray.enums.LockMode.VERIFY_PASSWORD;

/**
 * Created by Admin on 2018/4/10.
 */
@ContentView(R.layout.activity_gesture)
public class GestureDetailActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi {

    private CustomLockView mLvLock;
    private TextView tvHint;

    @Override
    public void initTitle() {
        isShowToolBar(true);
        setCenterTitle("请绘制");
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {
        mLvLock = findViewById(R.id.lv_lock);
        tvHint = findViewById(R.id.tv_hint);
        //设置模式
        LockMode lockMode = (LockMode) getIntent().getSerializableExtra(Constants.INTENT_SECONDACTIVITY_KEY);
        setLockMode(lockMode);



        //显示绘制方向
        mLvLock.setShow(true);
        //允许最大输入次数
        mLvLock.setErrorNumber(3);
        //密码最少位数
        mLvLock.setPasswordMinLength(4);
        //编辑密码或设置密码时，是否将密码保存到本地，配合setSaveLockKey使用
        mLvLock.setSavePin(true);
        //保存密码Key
        mLvLock.setSaveLockKey(Constants.PASS_KEY);


        mLvLock.setOnCompleteListener(onCompleteListener);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void initEvent() {

    }

    /**
     * 密码输入监听
     */
    CustomLockView.OnCompleteListener onCompleteListener = new CustomLockView.OnCompleteListener() {
        @Override
        public void onComplete(String password, int[] indexs) {
            tvHint.setText(getPassWordHint());
            if (mLvLock.getMode() == LockMode.SETTING_PASSWORD){
                EventBus.getDefault().post(new SSSetEvent());
            }else if (mLvLock.getMode() == LockMode.CLEAR_PASSWORD){
                EventBus.getDefault().post(new SSClearEvent());
            }else if (mLvLock.getMode() == LockMode.VERIFY_PASSWORD){
                ARouter.getInstance()
                        .build("/center/HomeActivity")
                        .navigation();
            }
            finish();
        }

        @Override
        public void onError(String errorTimes) {
            tvHint.setText("密码错误，还可以输入" + errorTimes + "次");
        }

        @Override
        public void onPasswordIsShort(int passwordMinLength) {
            tvHint.setText("密码不能少于" + passwordMinLength + "个点");
        }

        @Override
        public void onAginInputPassword(LockMode mode, String password, int[] indexs) {
            tvHint.setText("请再次输入密码");
        }
        @Override
        public void onInputNewPassword() {
            tvHint.setText("请输入新密码");
        }

        @Override
        public void onEnteredPasswordsDiffer() {
            tvHint.setText("两次输入的密码不一致");
        }

        @Override
        public void onErrorNumberMany() {
            tvHint.setText("密码错误次数超过限制，不能再输入");
        }

    };
    /**
     * 密码相关操作完成回调提示
     */
    private String getPassWordHint() {
        String str = null;
        switch (mLvLock.getMode()) {
            case SETTING_PASSWORD:
                str = "密码设置成功";
                break;
            case EDIT_PASSWORD:
                str = "密码修改成功";
                break;
            case VERIFY_PASSWORD:
                str = "密码正确";
                break;
            case CLEAR_PASSWORD:
                str = "密码已经清除";
                break;
        }
        return str;
    }

    /**
     * 设置解锁模式
     */
    private void setLockMode(LockMode mode) {
        String str = "";
        switch (mode) {
            case CLEAR_PASSWORD:
                str = "清除密码";
                setLockMode(LockMode.CLEAR_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case EDIT_PASSWORD:
                str = "修改密码";
                setLockMode(EDIT_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case SETTING_PASSWORD:
                str = "设置密码";
                setLockMode(LockMode.SETTING_PASSWORD, null, str);
                break;
            case VERIFY_PASSWORD:
                str = "验证密码";
                setLockMode(VERIFY_PASSWORD, PasswordUtil.getPin(this), str);
                break;
        }
        setCenterTitle(str);
    }
    /**
     * 密码输入模式
     */
    private void setLockMode(LockMode mode, String password, String msg) {
        mLvLock.setMode(mode);
        mLvLock.setErrorNumber(3);
//        mLvLock.setclea(false);
        if (mode != SETTING_PASSWORD) {
            tvHint.setText("请输入已经设置过的密码");
            mLvLock.setOldPassword(password);
        } else {
            tvHint.setText("请输入要设置的密码");
        }
        setCenterTitle(msg);
    }

}
