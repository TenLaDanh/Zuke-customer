package vn.edu.tdc.zuke_customer.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.CommentAdapter;
import vn.edu.tdc.zuke_customer.adapters.ProductAdapter;
import vn.edu.tdc.zuke_customer.data_models.OfferDetail;
import vn.edu.tdc.zuke_customer.data_models.Product;
import vn.edu.tdc.zuke_customer.data_models.Rating;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến:
    String accountID = "abc05684428156";
    Toolbar toolbar;
    Product item = null;
    Intent intent;
    TextView subtitleAppbar, price, sold, price_main, name, description, rating, detail;
    ImageView imgProduct, addCart, hotline;
    ToggleButton button_favorite;
    Button btnMuaNgay;
    RatingBar simpleRatingBar;
    RecyclerView relateProduct, rcvComment;
    ConstraintLayout conRating;
    ProductAdapter productRelate;
    ArrayList<Product> listRelate;
    ArrayList<Rating> listComment;
    CommentAdapter commentAdapter;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference offerDetailRef = db.getReference("Offer_Details");
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference ratingRef = db.getReference("Rating");
    DatabaseReference favoriteRef = db.getReference("Favorite");
    Query queryComment, queryRelate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_product);

        // Toolbar:
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleDMSP);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Lấy dữ liệu được gửi sang:
        intent = getIntent();
        item = intent.getParcelableExtra("item");

        // Khởi tạo biến:
        imgProduct = findViewById(R.id.imgProduct);
        price = findViewById(R.id.price);
        button_favorite = findViewById(R.id.button_favorite);
        sold = findViewById(R.id.sold);
        price_main = findViewById(R.id.price_main);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        simpleRatingBar = findViewById(R.id.simpleRatingBar);
        rating = findViewById(R.id.rating);
        relateProduct = findViewById(R.id.relateProduct);
        btnMuaNgay = findViewById(R.id.btnMuaNgay);
        addCart = findViewById(R.id.addCart);
        hotline = findViewById(R.id.hotline);
        detail = findViewById(R.id.detail);
        rcvComment = findViewById(R.id.rcvComment);
        conRating = findViewById(R.id.conRating);
        listComment = new ArrayList<>();
        listRelate = new ArrayList<>();
        productRelate = new ProductAdapter(this, listRelate);
        commentAdapter = new CommentAdapter(this, listComment);

        // Sự kiện cho click cho các đối tượng:
        btnMuaNgay.setOnClickListener(this);
        detail.setOnClickListener(this);
        hotline.setOnClickListener(this);
        addCart.setOnClickListener(this);
        button_favorite.setOnClickListener(this);

        // Recycleview:
        rcvComment.setHasFixedSize(true);
        relateProduct.setHasFixedSize(true);

        // Đổ dữ liệu vào recyclerView:
        relateProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relateProduct.setAdapter(productRelate);

        rcvComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvComment.setAdapter(commentAdapter);

        // Kiểm tra nếu nhận được đối tượng tiến hành đổ dữ liệu:
        if (item != null) {
            data();
            // Truyền thông tin product vào các trường
            // Thông tin sản phẩm:
            // Tên:
            name.setText(item.getName());
            // Load ảnh
            StorageReference imageRef = FirebaseStorage.getInstance()
                    .getReference("images/products/" + item.getName() + "/" + item.getImage());
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).fit().into(imgProduct));

            //Giá
            offerDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int maxSale = 0;
                    for (DataSnapshot node1 : snapshot.getChildren()) {
                        OfferDetail detail = node1.getValue(OfferDetail.class);
                        if (detail.getProductID().equals(item.getKey())) {
                            if (detail.getPercentSale() > maxSale) {
                                maxSale = detail.getPercentSale();
                            }
                        }
                    }
                    if (maxSale != 0) {
                        int discount = item.getPrice() / 100 * (100 - maxSale);
                        price.setText(formatPrice(discount));
                        price_main.setText(formatPrice(item.getPrice()));
                        price_main.setPaintFlags(price_main.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        price.setText(formatPrice(item.getPrice()));
                        price_main.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //Rating:
            if (item.getRating() > 0) {
                rating.setText(item.getRating() + "/5");
                simpleRatingBar.setRating(item.getRating());
            } else {
                conRating.setVisibility(View.GONE);
            }

            //Mô tả:
            description.setText(item.getDescription());

            //Đã bán:
            if (item.getSold() > 0) {
                sold.setText(item.getSold() + " đã bán");
            } else {
                sold.setVisibility(View.GONE);
            }
            // Kiểm tra trong yêu thích:
            favoriteRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        if (node.child("userId").getValue(String.class).equals(accountID)
                                && node.child("productId").getValue(String.class).equals(item.getKey())) {
                            button_favorite.setChecked(true);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private String formatPrice(int price) {
        String s = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
        return s.substring(2, s.length()) + " ₫";
    }

    private void data() {
        // Lấy list comment limit 5:
        queryComment = ratingRef.orderByChild("productID").equalTo(item.getKey()).limitToLast(3);
        queryComment.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Rating rating = node.getValue(Rating.class);
                    if (rating.getProductID().equals(item.getKey())) {
                        listComment.add(rating);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Lấy list sản phẩm liên quan:
        queryRelate = proRef.orderByChild("category_id").equalTo(item.getCategory_id()).limitToLast(5);
        queryRelate.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listRelate.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product product = node.getValue(Product.class);
                    product.setKey(node.getKey());
                    if (product.getCategory_id().equals(item.getCategory_id()) && !product.getKey().equals(item.getKey())) {
                        listRelate.add(product);
                    }
                }
                productRelate.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
