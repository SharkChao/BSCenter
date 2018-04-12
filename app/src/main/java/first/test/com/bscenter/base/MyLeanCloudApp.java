package first.test.com.bscenter.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;

import javax.inject.Inject;

import cn.bmob.v3.Bmob;
import first.test.com.bscenter.inject.component.AppComponent;
import first.test.com.bscenter.inject.component.DaggerAppComponent;
import first.test.com.bscenter.inject.module.ApiServiceModule;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.AppUtils;

/**
 */

public class MyLeanCloudApp extends Application{
    private static MyLeanCloudApp mLeanCloudApp;
    @Inject
    MainPresenter mMainPresenter;
    @Override
    public void onCreate() {
        super.onCreate();
        mLeanCloudApp = this;
        Bmob.initialize(this, "9429c5e5ab59f91a8fe65c5205d1a5e1");
        if (AppUtils.isDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
//        Stetho.initializeWithDefaults(this);
        AppComponent build = DaggerAppComponent.builder().apiServiceModule(new ApiServiceModule())
                .build();
        build.inject(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static MyLeanCloudApp getInstance(){
        return mLeanCloudApp;
    }
    public MainPresenter getMainPresenter(){
        return mMainPresenter;
    }
}
