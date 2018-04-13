package first.test.com.bscenter.fragment;

import java.io.File;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.util.FaceUtil;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import first.test.com.bscenter.BuildConfig;
import first.test.com.bscenter.R;
import first.test.com.bscenter.activity.FaceDetailActivity;
import first.test.com.bscenter.activity.GestureDetailActivity;
import first.test.com.bscenter.activity.main.ApksActivity;
import first.test.com.bscenter.activity.main.MainActivity;
import first.test.com.bscenter.adapter.FavoriteListViewAdapter;
import first.test.com.bscenter.adapter.FileListAdapter;
import first.test.com.bscenter.constants.Constants;
import first.test.com.bscenter.core.common.FileType;
import first.test.com.bscenter.core.engine.ResourceManager;
import first.test.com.bscenter.dao.DaoFactory;
import first.test.com.bscenter.dao.impl.FavoriteDao;
import first.test.com.bscenter.event.FaceClearEvent;
import first.test.com.bscenter.event.FaceSetEvent;
import first.test.com.bscenter.event.FaceVerifyEvent;
import first.test.com.bscenter.event.SSClearEvent;
import first.test.com.bscenter.event.SSSetEvent;
import first.test.com.bscenter.event.SSVerifyEvent;
import first.test.com.bscenter.model.Favorite;
import first.test.com.bscenter.utils.CommonUtil;
import first.test.com.bscenter.utils.PasswordUtil;
import first.test.com.bscenter.views.FingerPrinterView;
import first.test.com.bscenter.views.PhotoPopupWindow;
import io.reactivex.observers.DisposableObserver;
import zwh.com.lib.FPerException;
import zwh.com.lib.RxFingerPrinter;


public class FavoritePageFragment extends Fragment implements FileListAdapter.OnCheckBoxChangedListener, OnClickListener {
    private View mView = null;
    private View mFavoriteBottomDelete = null;
    private View mLayoutSelectAll = null;
    private ImageView mImageFavoriteSelectAll = null;
    private TextView mTvFavoriteSelectAll = null;
    private ImageButton mImgBtnBack = null;
    private View mNothingView = null;

    private ListView mListView = null;
    private FavoriteListViewAdapter mAdapter = null;
    private List<Favorite> mFavorites = null;
    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    private PhotoPopupWindow mPopupWindow;
    private AlertDialog mAlertDialog;
    private Button btnJS;
    private AlertDialog mAlertDialog1;
    private AlertDialog mAlertDialog2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.page_favorite, null);

       EventBus.getDefault().register(this);
        mImageFavoriteSelectAll = (ImageView) mView.findViewById(R.id.mImageFavoriteSelectAll);
        mImageFavoriteSelectAll.setTag(false);
        mTvFavoriteSelectAll = (TextView) mView.findViewById(R.id.mTvFavoriteSelectAll);
        btnJS = mView.findViewById(R.id.btnJS);
        mListView = (ListView) mView.findViewById(R.id.mListViewFavorit);
        mNothingView = mView.findViewById(R.id.nothingFavorite);
        
        mFavoriteBottomDelete = mView.findViewById(R.id.mFavoriteBottomDelete);
        mFavoriteBottomDelete.setOnClickListener(this);
        mImgBtnBack =  (ImageButton) mView.findViewById(R.id.mImgBtnBack);
        mImgBtnBack.setOnClickListener(this);

        btnJS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        mLayoutSelectAll = mView.findViewById(R.id.mLayoutSelectAll);
        mLayoutSelectAll.setOnClickListener(this);

        mFavorites = ResourceManager.getAllFavorites();
        if(mFavorites.size() == 0) {
            mNothingView.setVisibility(View.GONE);
        } else {
            mNothingView.setVisibility(View.GONE);
        }
        mAdapter = new FavoriteListViewAdapter(mView.getContext(), mFavorites, mListView, (MainActivity) getActivity());
        mAdapter.setOnCheckBoxChangedListener(this);
        mListView.setAdapter(mAdapter);
        isInit = true;
        /**初始化的时候去加载数据**/
        isCanLoadData();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        new EventBus().unregister(this);
    }

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected  void lazyLoad(){
        mListView.setVisibility(View.INVISIBLE);
        btnJS.setVisibility(View.VISIBLE);
        mLayoutSelectAll.setVisibility(View.GONE);
        showDialog();
    }

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected void stopLoad() {
    }
    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }
    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    public void startListAnim() {
        mListView.startLayoutAnimation();
    }

    public void reLoadFavoriteList() {
        mFavorites = ResourceManager.getAllFavorites();
        if(mFavorites.size() == 0) {
            mNothingView.setVisibility(View.GONE);
        } else {
            mNothingView.setVisibility(View.GONE);
        }
        Collections.reverse(mFavorites);
        
        mImageFavoriteSelectAll.setTag(false);
        mAdapter = new FavoriteListViewAdapter(mView.getContext(), mFavorites, mListView, (MainActivity) getActivity());
        mAdapter.setOnCheckBoxChangedListener(this);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setSelectImage();
    }

    @Override
    public void onCheckChanged(int position, boolean isChecked) {
        setSelectImage();
    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId()) {
        case R.id.mLayoutSelectAll:
            boolean isSelectAll = (Boolean) mImageFavoriteSelectAll.getTag();
            if(isSelectAll) {
                setSelectItems(false);
            }else {
                setSelectItems(true);
            }
            setSelectImage();
            mAdapter.notifyDataSetChanged();
            break;
        case R.id.mFavoriteBottomDelete:
            deletSelectedItem();
            break;
            
        case R.id.mImgBtnBack:
            ((MainActivity)getActivity()).goToPage(0);
            break;

        default:
            break;
        }
    }
    
    private void deletSelectedItem() {
        for (int i = 0; i < mFavorites.size(); i++) {
            if(mFavorites.get(i).isChecked()) {
                ResourceManager.removeItem(mFavorites.get(i).getCanonicalPath());
                mFavorites.remove(i);
                i--;
            }
        }
        mAdapter.notifyDataSetChanged();
        setSelectImage();
    }

    private void setSelectItems(boolean selecte) {
        for (Favorite favorite : mFavorites) {
            favorite.setChecked(selecte);
        }
    }

    public void setSelectImage() {
        if(isSelectAll()) {
            
            mImageFavoriteSelectAll.setBackgroundResource(R.drawable.op_select_nothing); 
            mTvFavoriteSelectAll.setText("取消");
            mImageFavoriteSelectAll.setTag(true);
        } else {
            mImageFavoriteSelectAll.setBackgroundResource(R.drawable.op_select_all); 
            mTvFavoriteSelectAll.setText("全选");
            mImageFavoriteSelectAll.setTag(false);
        }
        
        if(isSelectOne()) {
            mFavoriteBottomDelete.setVisibility(View.VISIBLE);
        } else {
            mFavoriteBottomDelete.setVisibility(View.GONE);
        }
    }

    private boolean isSelectOne() {
        for (Favorite favorite : mFavorites) {
            if (favorite.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSelectAll() {
        for (Favorite favorite : mFavorites) {
            if (!favorite.isChecked()) {
                return false;
            }
        }
        return true;
    }


    private Button mRvSetting;
    private Button mRvSettingFace;
    public void showAlert1(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_view1,null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        final FingerPrinterView fingerPrinterView = (FingerPrinterView)view.findViewById(R.id.fpv);
        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override public void onChange(int state) {
                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                    Toast.makeText(getActivity(), "指纹识别成功", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    setAction();
                }
                if (state == FingerPrinterView.STATE_WRONG_PWD) {
                    Toast.makeText(getActivity(), "指纹识别失败，请重试",
                            Toast.LENGTH_SHORT).show();
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                }
            }
        });
        RxFingerPrinter rxfingerPrinter = new RxFingerPrinter(getActivity());
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
                    Toast.makeText(getActivity(),((FPerException) e).getDisplayMessage(),Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_view2, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        mRvSetting = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);

        String pin = PasswordUtil.getPin(getActivity());
        mRvSetting.setVisibility(CommonUtil.isStrEmpty(pin)?View.VISIBLE:View.GONE);
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
        mRvSetting.setOnClickListener(new View.OnClickListener() {
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
        mAlertDialog2 = builder.create();
        mAlertDialog2.show();
    }
    /**
     * 跳转到密码处理界面
     */
    private void actionSecondActivity(LockMode mode) {
        if (mode != LockMode.SETTING_PASSWORD) {
            if (StringUtils.isEmpty(PasswordUtil.getPin(getActivity()))) {
                Toast.makeText(getActivity(), "请先设置密码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(getActivity(), GestureDetailActivity.class);
        intent.putExtra(Constants.INTENT_SECONDACTIVITY_KEY, mode);
        intent.putExtra(Constants.INTENT_type_KEY, 1);
        startActivity(intent);
    }

    private void showAlert3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_view3, null);

        Button rvClear = (Button) contentView.findViewById(R.id.btn4);
        Button rvEdit = (Button) contentView.findViewById(R.id.btn3);
        mRvSettingFace = (Button) contentView.findViewById(R.id.btn1);
        Button rvVerify = (Button) contentView.findViewById(R.id.btn2);
        Bitmap bitmap = FaceUtil.getImage(getActivity(), Constants.FACE_FILE_NAME_SAVE);
        mRvSettingFace.setVisibility(CommonUtil.isNotEmpty(bitmap)?View.GONE:View.VISIBLE);
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
        mRvSettingFace.setOnClickListener(new View.OnClickListener() {
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
        mAlertDialog1 = builder.create();
        mAlertDialog1.show();

    }

    private void actionFaceActivity(int value){
        // 要校验的权限
        if (value != Constants.FACE_MODE_SET) {
            Bitmap bitmap = FaceUtil.getImage(getActivity(), Constants.FACE_FILE_NAME_SAVE);
            if (!CommonUtil.isNotEmpty(bitmap)) {
                Toast.makeText(getActivity(), "请先设置人脸特征!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(getActivity(),FaceDetailActivity.class);
        intent.putExtra("face_key",value);
        intent.putExtra(Constants.INTENT_type_KEY, 1);
        startActivity(intent);
    }

    public void showDialog(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.main_verify_dialog,null);
        Button btn1 = (Button) view.findViewById(R.id.btn1);
        Button btn2 = (Button) view.findViewById(R.id.btn2);
        Button btn3 = (Button) view.findViewById(R.id.btn3);
        Button btn4 = (Button) view.findViewById(R.id.btn4);

        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert1();
            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert2();
            }
        });
        btn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert3();
            }
        });
        btn4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                showAlert4();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setView(view);
        builder.setCancelable(true);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSSetEvent event) {
        if (event != null){
            if (mRvSetting  != null){
                mRvSetting.setVisibility(View.GONE);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSVerifyEvent event) {
        if (event != null){
//            if (mRvSetting  != null){
//                mRvSetting.setVisibility(View.VISIBLE);
//            }
            setAction();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SSClearEvent event) {
        if (event != null){
            if (mRvSetting  != null){
                mRvSetting.setVisibility(View.VISIBLE);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceSetEvent event) {
        if (event != null){
            if (mRvSettingFace  != null){
                mRvSettingFace.setVisibility(View.GONE);
            }
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceVerifyEvent event) {
        if (event != null){
//            if (mRvSetting  != null){
//                mRvSetting.setVisibility(View.VISIBLE);
//            }
            setAction();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceClearEvent event) {
        if (event != null){
            if (mRvSettingFace  != null){
                mRvSettingFace.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setAction(){
        mListView.setVisibility(View.VISIBLE);
       mAlertDialog.dismiss();
       btnJS.setVisibility(View.GONE);
        mLayoutSelectAll.setVisibility(View.VISIBLE);
        if (mAlertDialog1 != null && mAlertDialog1.isShowing()){
            mAlertDialog1.dismiss();
        }
        if (mAlertDialog2 != null && mAlertDialog2.isShowing()){
            mAlertDialog2.dismiss();
        }
    }


}
