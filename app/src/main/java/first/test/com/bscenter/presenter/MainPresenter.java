package first.test.com.bscenter.presenter;


import first.test.com.bscenter.base.BasePresenter;
import first.test.com.bscenter.network.ApiService;

/**
 */

public class MainPresenter extends BasePresenter<MainPresenter.MainUi,MainPresenter.MainUiCallback> {

    private static MainPresenter mMainPresenter;
    public static MainPresenter getInstance(ApiService apiService){
        if (mMainPresenter == null){
            mMainPresenter = new MainPresenter(apiService);
        }
        return mMainPresenter;
    }
    private ApiService mApiService;
    private  MainPresenter(ApiService apiService){
        mApiService = apiService;
    }

    //获取数据之后回调
    public interface MainUiCallback{

    }

    @Override
    protected MainUiCallback createUiCallbacks(final MainUi ui) {
        return new MainUiCallback() {


        };
    }

    //给具体ui调用
    public interface MainUi extends BasePresenter.BaseUi<MainUiCallback>{

    }

}
