package com.zero.input.validator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zero.input.validator.annotations.Date;
import com.zero.input.validator.annotations.Email;
import com.zero.input.validator.annotations.IDCard;
import com.zero.input.validator.annotations.Mobile;
import com.zero.input.validator.annotations.NotEmpty;
import com.zero.input.validator.annotations.NextButton;
import com.zero.input.validator.annotations.OnValid;
import com.zero.input.validator.annotations.PassWord;
import com.zero.input.validator.annotations.TelPhone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by woshi on 2018/4/24.
 */

public class MainActivity extends Activity {
    @BindView(R.id.et_pwd)
    @PassWord(scheme = PassWord.Scheme.ANY, errMsg = R.string.error_pwd)
    EditText etPwd;

    @BindView(R.id.et_empty)
    @NotEmpty(errMsg = R.string.not_empty)
    EditText etName;

    @BindView(R.id.et_id_card)
    @IDCard()
    EditText etIDCard;

    @BindView(R.id.et_date)
    @Date(errMsg = R.string.error_date)
    EditText etDate;

    @BindView(R.id.et_email)
    @Email(errMsg = R.string.error_email)
    EditText etEmail;

    @BindView(R.id.et_mobile)
    @Mobile()
    EditText etMobile;

    @BindView(R.id.et_tel)
    @TelPhone()
    EditText etTel;

    @BindView(R.id.validate)
    @NextButton(enableBgColor = R.color.enableBgColor, enableTextColor = R.color.enableTextColor,
            disableBgColor = R.color.disableBgColor, disableTextColor = R.color.disableTextColor)
    TextView btn;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder=ButterKnife.bind(this);
        InputValidator.initInput(this);
    }

    @OnValid
    void validate() {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SecondActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputValidator.recycle();
        unbinder.unbind();
    }
}
