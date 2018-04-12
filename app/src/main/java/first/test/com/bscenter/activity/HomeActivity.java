package first.test.com.bscenter.activity;

import android.databinding.ViewDataBinding;


import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.presenter.MainPresenter;


@ContentView(R.layout.activity_home)
public class HomeActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi {


    @Override
    public void initTitle() {
        isShowToolBar(true);
        setCenterTitle("文件夹");
        isShowLeft(false);
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void initEvent() {

    }
}
