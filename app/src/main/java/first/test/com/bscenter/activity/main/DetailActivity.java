package first.test.com.bscenter.activity.main;

import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.databinding.ActivityDetailBinding;
import first.test.com.bscenter.fragment.FileListPageFragment;
import first.test.com.bscenter.presenter.MainPresenter;

/**
 * Created by Admin on 2018/4/13.
 */

@ContentView(R.layout.activity_detail)
public class DetailActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi{

    private ActivityDetailBinding mBinding;
    private FrameLayout mContent;

    @Override
    public void initTitle() {
        isShowToolBar(true);
        setCenterTitle("文件详情");
        isShowLeft(false);
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {
        mBinding = (ActivityDetailBinding) viewDataBinding;
        mContent = mBinding.content;
    }

    @Override
    public void initData() {
        String detail_file_path = getIntent().getStringExtra("detail_file_path");
        FileListPageFragment fragment = new FileListPageFragment();
//        fragment.autoDeploymentTopNavisStack(detail_file_path);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    @Override
    protected void initEvent() {

    }
}
