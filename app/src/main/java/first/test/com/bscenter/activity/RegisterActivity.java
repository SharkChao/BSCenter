package first.test.com.bscenter.activity;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.CommonUtil;

/**
 * Created by Admin on 2018/4/11.
 */
@Route(path = "/center/RegisterActivity")
@ContentView(R.layout.activity_register)

public class RegisterActivity extends BaseActivity<MainPresenter.MainUiCallback> implements MainPresenter.MainUi{

    private Button btnRegister;
    private EditText etName;
    private EditText etPassword;
    private ActivityRegisterBinding mBinding;

    @Override
    public void initTitle() {
        isShowToolBar(false);
    }

    @Override
    public void initView(ViewDataBinding viewDataBinding) {
        mBinding = (ActivityRegisterBinding) viewDataBinding;
        btnRegister = mBinding.btnRegister;
        etName = mBinding.edtAccount;
        etPassword = mBinding.edtPassword;
    }

    @Override
    public void initData() {

    }

    @Override
    protected void initEvent() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtil.isStrEmpty(etName.getText().toString())){
                    CommonUtil.showSnackBar(btnRegister,"请先输入账号");
                    return;
                }
                if (CommonUtil.isStrEmpty(etPassword.getText().toString())){
                    CommonUtil.showSnackBar(btnRegister,"请先输入密码");
                    return;
                }
                BmobUser bmobUser = new BmobUser();
                bmobUser.setUsername(etName.getText().toString());
                bmobUser.setPassword(etPassword.getText().toString());
                bmobUser.signUp(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e == null){
                            Toast.makeText(RegisterActivity.this, "恭喜您，注册成功!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(RegisterActivity.this, "注册失败"+e.getErrorCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
