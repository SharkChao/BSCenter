package first.test.com.bscenter.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.kongqw.interfaces.OnFaceDetectorListener;
import com.kongqw.interfaces.OnOpenCVInitListener;
import com.kongqw.util.FaceUtil;
import com.kongqw.view.CameraFaceDetectionView;

import org.greenrobot.eventbus.EventBus;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.constants.Constants;
import first.test.com.bscenter.databinding.ActivityFaceDetailBinding;
import first.test.com.bscenter.event.FaceClearEvent;
import first.test.com.bscenter.event.FaceSetEvent;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.PermisionUtils;

/**
 * Created by Admin on 2018/4/11.
 */
@ContentView(R.layout.activity_face_detail)
@Route(path = "/center/FaceDetailActivity")
public class FaceDetailActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi{

    final String TAG = "face_tag";
    private int mMode;
    private ActivityFaceDetailBinding mBinding;
    private TextView tvHint;
    private CameraFaceDetectionView mCameraFaceDetectionView;

    @Override
    public void initTitle() {
        isShowToolBar(true);
        mMode = getIntent().getIntExtra("face_key", 0);
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {
        PermisionUtils.verifyCameraPermissions(this);
        mBinding = (ActivityFaceDetailBinding) viewDataBinding;
        tvHint = mBinding.tvHint;
        mCameraFaceDetectionView = mBinding.cameraFaceDetectionView;
    }

    @Override
    public void initData() {
        setCenterMode();
    }

    @Override
    protected void initEvent() {
//        mCameraFaceDetectionView.switchCamera();
        mCameraFaceDetectionView.setOnFaceDetectorListener(new OnFaceDetectorListener() {
            @Override
            public void onFace(Mat mat, Rect rect) {
                if (mMode == Constants.FACE_MODE_SET){
                    boolean isSave = FaceUtil.saveImage(FaceDetailActivity.this,  mat,  rect, Constants.FACE_FILE_NAME_SAVE);
                    if (isSave){
                        Toast.makeText(FaceDetailActivity.this, "采集人脸特征成功!", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new FaceSetEvent());
                        finish();
                    }else {
                        Toast.makeText(FaceDetailActivity.this, "采集人脸特征失败,请重试!", Toast.LENGTH_SHORT).show();
                    }
                }else if (mMode == Constants.FACE_MODE_CLEAR){
                    boolean isNews = FaceUtil.saveImage(FaceDetailActivity.this,  mat,  rect, Constants.FACE_FILE_NAME_SAVE);
                    if (isNews){
                        double score = FaceUtil.compare(FaceDetailActivity.this, Constants.FACE_FILE_NAME_SAVE, Constants.FACE_FILE_NAME_NEW);
                        if (score >= 70){
                            new AlertDialog.Builder(FaceDetailActivity.this)
                                    .setTitle("提示")
                                    .setMessage("您确定要重置人脸特征吗?")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            boolean isSave = FaceUtil.deleteImage(FaceDetailActivity.this, Constants.FACE_FILE_NAME_SAVE);
                                            boolean isSave2 = FaceUtil.deleteImage(FaceDetailActivity.this, Constants.FACE_FILE_NAME_NEW);
                                            if (isSave && isSave2){
                                                Toast.makeText(FaceDetailActivity.this, "清除人脸特征成功!", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().post(new FaceClearEvent());
                                            }
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                        }
                    }else {
                        Toast.makeText(FaceDetailActivity.this, "采集人脸特征失败,请重试!", Toast.LENGTH_SHORT).show();
                    }


                }else if (mMode == Constants.FACE_MODE_VERFIY){
                    boolean isNews = FaceUtil.saveImage(FaceDetailActivity.this,  mat,  rect, Constants.FACE_FILE_NAME_SAVE);
                    if (isNews){
                        double score = FaceUtil.compare(FaceDetailActivity.this, Constants.FACE_FILE_NAME_SAVE, Constants.FACE_FILE_NAME_NEW);
                        if (score >= 70){
                            ARouter.getInstance()
                                    .build("/center/HomeActivity")
                                    .navigation();
                            finish();
                            Toast.makeText(FaceDetailActivity.this, "登陆成功!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(FaceDetailActivity.this, "人脸相似度为:"+score+",系统认定为不匹配!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        mCameraFaceDetectionView.setOnOpenCVInitListener(new OnOpenCVInitListener() {
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

        mCameraFaceDetectionView.loadOpenCV(getApplicationContext());
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
        tvHint.setText("请正脸看屏幕，避免光线直射");
    }
}
