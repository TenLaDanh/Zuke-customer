package vn.edu.tdc.zuke_customer.activitys;

import android.os.Bundle;

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
import vn.edu.tdc.zuke_customer.data_models.Order;

public class HistoryOrderActivity extends AppCompatActivity {
    String userID = "-MmB8LnpJ6Fjstfl1jzw";
    RecyclerView recyclerView;
    ArrayList<Order> list;
    OrderAdapter adapter;
    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);

        // Đổ dữ liệu recycleview:
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new OrderAdapter( list, this, userID);
        data();
        adapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private OrderAdapter.ItemClickListener itemClickListener = new OrderAdapter.ItemClickListener() {
        @Override
        public void getInfor(Order item) {

        }

        @Override
        public void getRating(String key) {

        }
    };

    public void data() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    list.add(order);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
