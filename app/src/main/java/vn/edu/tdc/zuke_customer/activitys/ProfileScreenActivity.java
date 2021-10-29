package vn.edu.tdc.zuke_customer.activitys;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdc.zuke_customer.R;

public class ProfileScreenActivity extends AppCompatActivity {
    String accountID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);
    }

    private void data() {
        // Lấy dữ liệu tên khách hàng:
        // Lấy dữ liệu đơn hàng đã mua:
        // Lấy dữ liệu đơn hàng hoàn thành:
        // Lấy dữ liệu đơn hàng huỷ:
    }
}
