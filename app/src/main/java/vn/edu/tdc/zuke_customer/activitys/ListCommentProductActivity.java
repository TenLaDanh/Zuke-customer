package vn.edu.tdc.zuke_customer.activitys;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdc.zuke_customer.R;

public class ListCommentProductActivity extends AppCompatActivity {
    String productID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);
    }
}
