package first.test.com.bscenter.activity;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.os.Message;


import java.util.concurrent.TimeUnit;

import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.presenter.MainPresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * Created by Admin on 2018/4/11.
 */
@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi{
    @Override
    public void initTitle() {
        isShowToolBar(false);
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {

    }

    @Override
    public void initData() {

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }.sendEmptyMessageDelayed(1, 1000);
        //先创建被观察者，以及被观察者被注册之后发送的事件
    }

    @Override
    protected void initEvent() {

    }
}
