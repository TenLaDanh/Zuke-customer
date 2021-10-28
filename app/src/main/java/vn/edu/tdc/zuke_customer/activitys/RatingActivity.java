package vn.edu.tdc.zuke_customer.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    TextView title, mess;


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

        // Xử lý sự kiện nút "Lưu":
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorDialog("Xác nhận lưu đánh giá?");
            }
        });

        // Đổ dữ liệu recycleview:
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new OrderRatingAdapter(this, list);
        adapter.setTo(to);
        data();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private int checkError() {
        for(Rating rating : list) {
            if(rating.getComment().equals("") || rating.getRating() == 0) {
                showWarningDialog("Vui lòng nhập đủ các trường cho các đánh giá sản phẩm!");
                return -1;
            }
        }
        return 1;
    }

    private void showErrorDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RatingActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(RatingActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            if(checkError() == 1) {
                // Format ngày tạo rating
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();

                // Lưu rating
                for(Rating rating : list) {
                    ratingRef.child(rating.getKey()).child("comment").setValue(rating.getComment());
                    ratingRef.child(rating.getKey()).child("created_at").setValue(sdf.format(date));
                    ratingRef.child(rating.getKey()).child("rating").setValue(rating.getRating());
                }
                showSuccesDialog("Cảm ơn những đánh giá từ bạn. Chúng tôi sẽ tích cực hơn nữa để làm hài lòng những khách hàng đã yêu mến sử dụng dịch vụ của chúng tôi!");
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RatingActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(RatingActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            to = "read";
            adapter.setTo(to);
            adapter.notifyDataSetChanged();
            btnSave.setVisibility(View.GONE);
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showWarningDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RatingActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(RatingActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
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
