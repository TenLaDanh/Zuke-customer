package vn.edu.tdc.zuke_customer.activitys;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import vn.edu.tdc.zuke_customer.R;

public class LoginActivity extends AppCompatActivity {
    private ImageView fbButton;
    private EditText edtPhone, edtPass;
    private CircularProgressButton btnLogin;
    private TextView btnRegis;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = db.getReference("Account");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.layout_login);
        fbButton = findViewById(R.id.btnFB);
        edtPhone = findViewById(R.id.editTextPhone);
        edtPass = findViewById(R.id.editTextPassword);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FacebookAuthActivity.class);
                startActivity(intent);
            }
        });
    }

    public int checkError() {
        if (String.valueOf(edtPhone.getText()).equals("")) {
            //Thông báo "Số điện thoại không được để trống"
            return -1;
        }
        if (String.valueOf(edtPass.getText()).equals("")) {
            //Thông báo "Mật khẩu không được để trống"
            return -1;
        }
        return 1;
    }

    public void onRegister(View View) {


            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);

    }

    public void onForgotPass(View View) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    public void onSubmit(View View) {
        String phone = String.valueOf(edtPhone.getText());
        String pass = String.valueOf(edtPass.getText());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Account account = node.getValue(Account.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startActivity(new Intent(this, HomeScreenActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

}
