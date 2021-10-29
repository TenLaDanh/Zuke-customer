package vn.edu.tdc.zuke_customer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.OrderDetailAdapter;
import vn.edu.tdc.zuke_customer.data_models.Order;
import vn.edu.tdc.zuke_customer.data_models.OrderDetail;

public class DetailHistoryOrderActivity extends AppCompatActivity {
    // Khai báo biến:
    TextView txtTotal, txtDate, txtStatus, txtNote, txtName, txtAddress, txtPhone;
    Intent intent;
    Order item = null;
    RecyclerView recyclerView;
    ArrayList<OrderDetail> list;
    OrderDetailAdapter adapter;
    DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Status");
    DatabaseReference order_detailRef = FirebaseDatabase.getInstance().getReference("Order_Details");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_order);
        intent = getIntent();
        item = intent.getParcelableExtra("item");

        // Khởi tạo biến:
        txtTotal = findViewById(R.id.txt_tongtien);
        txtDate = findViewById(R.id.txt_date);
        txtStatus = findViewById(R.id.txt_status);
        txtNote = findViewById(R.id.txt_note);
        txtName = findViewById(R.id.txt_name);
        txtAddress = findViewById(R.id.txt_address);
        txtPhone = findViewById(R.id.txt_phone);

        // Đổ dữ liệu recycleview:
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new OrderDetailAdapter(this, list);
        data();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(item != null) {
            txtName.setText("Họ tên người nhận: " + item.getName());
            txtPhone.setText("Số điện thoại: " + item.getPhone());
            txtAddress.setText("Địa chỉ: " + item.getAddress());
            txtNote.setText(item.getNote());
            txtDate.setText(item.getCreated_at());
            txtTotal.setText(formatPrice(item.getTotal()));
            statusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(Integer.parseInt(snapshot.getKey()) == item.getStatus()) {
                            txtStatus.setText(snapshot.getValue(String.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void data() {
        order_detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    OrderDetail orderDetail = node.getValue(OrderDetail.class);
                    if(orderDetail.getOrderID().equals(item.getOrderID())) {
                        list.add(orderDetail);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String formatPrice(int price) {
        String s = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
        return s.substring(2, s.length()) + " ₫";
    }
}
