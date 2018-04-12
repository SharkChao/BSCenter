package first.test.com.bscenter.presenter;


import first.test.com.bscenter.base.BasePresenter;

/**
 */

public class MainPresenter extends BasePresenter<MainPresenter.MainUi,MainPresenter.MainUiCallback> {

    private static MainPresenter mMainPresenter;
    public static MainPresenter getInstance( ){
        if (mMainPresenter == null){
            mMainPresenter = new MainPresenter();
        }
        return mMainPresenter;
    }
    private  MainPresenter( ){
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
