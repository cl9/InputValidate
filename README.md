# InputValidate
使用apt工具生成输入验证的重复代码


#使用annotation里面的注解
````
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
````


#注解当所有输入框都成功后的回调方法
````
@OnValid
void validate() {
    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
}
````


#注解下一步按钮
````
@NextButton(enableBgColor = R.color.enableBgColor, enableTextColor = R.color.enableTextColor,
            disableBgColor = R.color.disableBgColor, disableTextColor = R.color.disableTextColor)
TextView btn;
````

#Adding to project
````
buildscript {
    repositories {
            jcenter()
            maven {url 'https://dl.bintray.com/woshifantuo3/maven'}
    }
}

api 'com.zero:inputvalidator:1.0.3'
api 'com.zero:inputvalidator-annotations:1.0.3'
annotationProcessor 'com.zero:inputvalidator-processor:1.0.3'
````
