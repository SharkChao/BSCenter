package first.test.com.bscenter.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import first.test.com.bscenter.R;


/**
 * 底部弹出选择图片
 * Created by yuzhijun on 2017/12/7.
 */
public class PhotoPopupWindow extends PopupWindow {
    private View mView; // PopupWindow 菜单布局
    private Context mContext; // 上下文参数
    private View.OnClickListener mListener1; // 相册选取的点击监听器
    private View.OnClickListener mListener2; // 相册选取的点击监听器
    private View.OnClickListener mListener3; // 相册选取的点击监听器
    private View.OnClickListener mListener4; // 相册选取的点击监听器
    private Button mBtn_cancel;

    public PhotoPopupWindow(Activity context, View.OnClickListener listener1, View.OnClickListener listener2,View.OnClickListener listener3,View.OnClickListener listener4) {
        super(context);
        this.mContext = context;
        this.mListener1 = listener1;
        this.mListener2 = listener2;
        this.mListener3 = listener3;
        this.mListener4 = listener4;
        Init();
    }

    /**
     * 设置布局以及点击事件
     */
    private void Init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.take_photo_popup_item, null);
        Button btn1 = (Button) mView.findViewById(R.id.btn1);
        Button btn2 = (Button) mView.findViewById(R.id.btn2);
        Button btn3 = (Button) mView.findViewById(R.id.btn3);
        Button btn4 = (Button) mView.findViewById(R.id.btn4);
        mBtn_cancel = (Button) mView.findViewById(R.id.icon_btn_cancel);

        btn1.setOnClickListener(mListener1);
        btn2.setOnClickListener(mListener2);
        btn3.setOnClickListener(mListener3);
        btn4.setOnClickListener(mListener4);
        mBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 导入布局
        this.setContentView(mView);
        // 设置动画效果
        this.setAnimationStyle(R.style.popupwindow_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
        // 单击弹出窗以外处 关闭弹出窗
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.ll_pop).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
//                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public void showCancle(boolean isShow){
        mBtn_cancel.setVisibility(isShow? View.VISIBLE:View.GONE);
    }

}
