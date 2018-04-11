package first.test.com.bscenter.activity;


import android.databinding.ViewDataBinding;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kongqw.interfaces.OnFaceDetectorListener;
import com.kongqw.interfaces.OnOpenCVInitListener;
import com.kongqw.view.CameraFaceDetectionView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.constants.Constants;
import first.test.com.bscenter.presenter.MainPresenter;

/**
 * Created by Admin on 2018/4/11.
 */
@ContentView(R.layout.activity_face_detail)
@Route(path = "/center/FaceDetailActivity")
public class FaceDetailActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi{

    final String TAG = "face_tag";
    private int mMode;

    @Override
    public void initTitle() {
        isShowToolBar(true);
        mMode = getIntent().getIntExtra("face_key", 0);
        setCenterMode();
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {

    }

    @Override
    public void initData() {

        CameraFaceDetectionView cameraFaceDetectionView = (CameraFaceDetectionView) findViewById(R.id.cameraFaceDetectionView);
        cameraFaceDetectionView.setOnFaceDetectorListener(new OnFaceDetectorListener() {
            @Override
            public void onFace(Mat mat, Rect rect) {

//                double score = FaceUtil.compare(LoginActivity.this, String fileName1, String fileName2);
//                boolean isSave = FaceUtil.saveImage(LoginActivity.this, mat, rect, Constants.FACE_FILE_NAME);
            }
        });
        cameraFaceDetectionView.setOnOpenCVInitListener(new OnOpenCVInitListener() {
            @Override
            public void onLoadSuccess() {
                Log.i(TAG, "onLoadSuccess: ");
            }

            @Override
            public void onLoadFail() {
                Log.i(TAG, "onLoadFail: ");
            }

            @Override
            public void onMarketError() {
                Log.i(TAG, "onMarketError: ");
            }

            @Override
            public void onInstallCanceled() {
                Log.i(TAG, "onInstallCanceled: ");
            }

            @Override
            public void onIncompatibleManagerVersion() {
                Log.i(TAG, "onIncompatibleManagerVersion: ");
            }

            @Override
            public void onOtherError() {
                Log.i(TAG, "onOtherError: ");
            }
        });
        cameraFaceDetectionView.loadOpenCV(getApplicationContext());
    }

    @Override
    protected void initEvent() {

    }

    private void setCenterMode(){
        String title = "";
        switch (mMode){
            case Constants.FACE_MODE_SET:
                title = "设置人脸特征";
                break;
            case Constants.FACE_MODE_VERFIY:
                title = "验证人脸特征";
                break;
            case Constants.FACE_MODE_EDIT:
                title = "修改人脸特征";
                break;
            case Constants.FACE_MODE_CLEAR:
                title = "清除人脸特征";
                break;
        }
        setCenterTitle(title);
    }
}
