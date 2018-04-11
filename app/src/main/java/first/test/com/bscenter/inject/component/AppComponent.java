package first.test.com.bscenter.inject.component;


import javax.inject.Singleton;

import dagger.Component;
import first.test.com.bscenter.base.MyLeanCloudApp;
import first.test.com.bscenter.inject.module.ApiServiceModule;
import first.test.com.bscenter.presenter.MainPresenter;

/**
 */
@Singleton
@Component(modules = {
        ApiServiceModule.class
})
public interface AppComponent {
    MainPresenter getMainPresenter();
    void inject(MyLeanCloudApp myLeanCloudApp);
}
