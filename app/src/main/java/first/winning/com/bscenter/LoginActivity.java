package first.winning.com.bscenter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.StringUtils;
import com.leo.gesturelibray.view.CustomLockView;

import first.winning.com.bscenter.util.PasswordUtil;

/**
 * Created by Admin on 2018/4/10.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        TextView tv3 = findViewById(R.id.tv3);
        TextView tv4 = findViewById(R.id.tv4);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_view1, null);

                RippleView rvClear = (RippleView) contentView.findViewById(R.id.rv_clear);
                RippleView rvEdit = (RippleView) contentView.findViewById(R.id.rv_edit);
                RippleView rvSetting = (RippleView) contentView.findViewById(R.id.rv_setting);
                RippleView rvVerify = (RippleView) contentView.findViewById(R.id.rv_verify);

                rvClear.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        actionSecondActivity(LockMode.CLEAR_PASSWORD);
                    }
                });
                rvEdit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        actionSecondActivity(LockMode.EDIT_PASSWORD);
                    }
                });
                rvSetting.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        actionSecondActivity(LockMode.SETTING_PASSWORD);
                    }
                });
                rvVerify.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        actionSecondActivity(LockMode.VERIFY_PASSWORD);
                    }
                });

                builder.setView(contentView);
                builder.create().show();
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 跳转到密码处理界面
     */
    private void actionSecondActivity(LockMode mode) {
        if (mode != LockMode.SETTING_PASSWORD) {
            if (StringUtils.isEmpty(PasswordUtil.getPin(this))) {
                Toast.makeText(getBaseContext(), "请先设置密码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
        intent.putExtra(Contants.INTENT_SECONDACTIVITY_KEY, mode);
        startActivity(intent);
    }
}
