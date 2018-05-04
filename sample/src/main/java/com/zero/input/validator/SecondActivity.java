package com.zero.input.validator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.zero.input.validator.annotations.NotEmpty;

/**
 * Created by woshi on 2018/5/4.
 */

public class SecondActivity extends AppCompatActivity {
    @NotEmpty()
    EditText etSex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        etSex = findViewById(R.id.et_sex);

    }
}
