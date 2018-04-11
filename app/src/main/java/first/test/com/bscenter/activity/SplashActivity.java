package first.test.com.bscenter.activity;

import android.databinding.ViewDataBinding;

import com.alibaba.android.arouter.launcher.ARouter;

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
        //先创建被观察者，以及被观察者被注册之后发送的事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(0);
            }
        }).delay(2, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer == 0){
                    ARouter.getInstance()
                            .build("/center/LoginActivity")
                            .navigation();
                    finish();
                }
            }
        });
    }

    @Override
    protected void initEvent() {

    }
}
