package com.zero.input.validator;

import android.app.Activity;
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

/**
 * Created by woshi on 2018/4/24.
 */

public class MainActivity extends Activity {
    @PassWord(scheme = PassWord.Scheme.ANY, errMsg = R.string.error_pwd)
    EditText etPwd;
    @NotEmpty(errMsg = R.string.not_empty)
    EditText etName;
    @IDCard()
    EditText etIDCard;
    @Date(errMsg = R.string.error_date)
    EditText etDate;
    @Email(errMsg = R.string.error_email)
    EditText etEmail;
    @Mobile()
    EditText etMobile;
    @TelPhone()
    EditText etTel;
    @NextButton(enableBgColor = R.color.enableBgColor, enableTextColor = R.color.enableTextColor,
            disableBgColor = R.color.disableBgColor, disableTextColor = R.color.disableTextColor)
    TextView btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPwd = findViewById(R.id.et_pwd);
        etName = findViewById(R.id.et_empty);
        etIDCard = findViewById(R.id.et_id_card);
        etDate = findViewById(R.id.et_date);
        etEmail = findViewById(R.id.et_email);
        etMobile = findViewById(R.id.et_mobile);
        etTel = findViewById(R.id.et_tel);
        btn = findViewById(R.id.validate);
        final MainActivity_InputValidator in = new MainActivity_InputValidator(MainActivity.this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in.validate();
            }
        });
    }

    @OnValid
    void validate() {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
    }

}
