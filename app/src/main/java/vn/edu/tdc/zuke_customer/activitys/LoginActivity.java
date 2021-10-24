package vn.edu.tdc.zuke_customer.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdc.zuke_customer.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.layout_login);
    }

    public void onRegister(View View){
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    public void onForgotPass(View View){
        startActivity(new Intent(this, ForgotPasswordActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    public void onSubmit(View View){
        startActivity(new Intent(this, HomeScreenActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }
}
