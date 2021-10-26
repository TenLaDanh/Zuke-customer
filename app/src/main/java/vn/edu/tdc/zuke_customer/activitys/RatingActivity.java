package vn.edu.tdc.zuke_customer.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.ArrayList;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.OrderAdapter;
import vn.edu.tdc.zuke_customer.adapters.OrderRatingAdapter;
import vn.edu.tdc.zuke_customer.data_models.Order;
import vn.edu.tdc.zuke_customer.data_models.Rating;

public class RatingActivity extends AppCompatActivity {
    String orderID = "", to = "";
    RecyclerView recyclerView;
    ArrayList<Rating> list;
    OrderRatingAdapter adapter;
    DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("Rating");
    Intent intent;
    Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rating);
        intent = getIntent();
        to = intent.getStringExtra("to");
        orderID = intent.getStringExtra("key");

        // Khởi tạo biến:
        btnSave = findViewById(R.id.button);
        if(to.equals("read")) {
            btnSave.setVisibility(View.GONE);
        }

        // Đổ dữ liệu recycleview:
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new OrderRatingAdapter(this, list, to);
        data();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void data() {
        ratingRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Rating rating = dataSnapshot.getValue(Rating.class);
                    rating.setKey(dataSnapshot.getKey());
                    if(rating.getOrderID().equals(orderID)) {
                        list.add(rating);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
