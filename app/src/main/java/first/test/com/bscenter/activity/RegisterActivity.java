package first.test.com.bscenter.activity;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import first.test.com.bscenter.R;
import first.test.com.bscenter.annotation.ContentView;
import first.test.com.bscenter.base.BaseActivity;
import first.test.com.bscenter.databinding.ActivityRegisterBinding;
import first.test.com.bscenter.greendao.DaoMaster;
import first.test.com.bscenter.greendao.DaoSession;
import first.test.com.bscenter.greendao.GreenDaoManager;
import first.test.com.bscenter.greendao.UserDao;
import first.test.com.bscenter.model.User;
import first.test.com.bscenter.presenter.MainPresenter;
import first.test.com.bscenter.utils.CommonUtil;

/**
 * Created by Admin on 2018/4/11.
 */
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

                User user = new User();
                user.setName(etName.getText().toString());
                user.setPassword(etPassword.getText().toString());

                DaoMaster master = GreenDaoManager.getInstance(RegisterActivity.this)
                        .getMaster();
                DaoSession daoSession = master.newSession();
                UserDao userDao = daoSession.getUserDao();
                User unique = userDao.queryBuilder().where(UserDao.Properties.Name.eq(user.getName())).unique();
                if (unique != null){
                    CommonUtil.showSnackBar(etName,"当前账号已经注册过，请切换账号后重试!");
                }else {
                    userDao.insert(user);
                    Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }
}
