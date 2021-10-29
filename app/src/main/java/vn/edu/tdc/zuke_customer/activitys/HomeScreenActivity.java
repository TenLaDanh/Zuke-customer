package vn.edu.tdc.zuke_customer.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collections;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.BannerAdapter;
import vn.edu.tdc.zuke_customer.adapters.CategoryAdapter;
import vn.edu.tdc.zuke_customer.adapters.Product2Adapter;
import vn.edu.tdc.zuke_customer.adapters.ProductAdapter;
import vn.edu.tdc.zuke_customer.data_models.Banner;
import vn.edu.tdc.zuke_customer.data_models.Category;
import vn.edu.tdc.zuke_customer.data_models.Product;

public class HomeScreenActivity extends AppCompatActivity {
    // Khai báo biến:
    RecyclerView recyclerCate, recyclerGoiY, recyclerMuaNhieu;
    ArrayList<Category> listCate;
    ArrayList<Product> listProductSold, listProductRating;
    ArrayList<Banner> listBanner;
    CategoryAdapter categoryAdapter;
    Product2Adapter productAdapterSold;
    ProductAdapter productAdapterRating;
    BannerAdapter bannerAdapter;
    SliderView imgHomeSlider;

    Query querySortBySold, querySortBySuggestion, queryBanner;
    DatabaseReference proRef = FirebaseDatabase.getInstance().getReference().child("Products");
    DatabaseReference cateRef = FirebaseDatabase.getInstance().getReference().child("Categories");
    DatabaseReference banRef = FirebaseDatabase.getInstance().getReference().child("Offers");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        // Khởi tạo biến:
        imgHomeSlider = findViewById(R.id.imageSlider);
        listCate = new ArrayList<>();
        listBanner = new ArrayList<>();
        listProductSold = new ArrayList<>();
        listProductRating = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, listCate);
        productAdapterSold = new Product2Adapter(this, listProductSold);
        productAdapterRating = new ProductAdapter(this, listProductRating);
        recyclerGoiY = findViewById(R.id.recycler_view1);
        recyclerCate = findViewById(R.id.recycler_view);
        recyclerMuaNhieu = findViewById(R.id.recycler_view2);

        // Gọi hàm lấy dữ liệu:
        data();

        // Recycleview:
        recyclerCate.setHasFixedSize(true);
        recyclerGoiY.setHasFixedSize(true);
        recyclerMuaNhieu.setHasFixedSize(true);

        // Đổ dữ liệu vào recyclerView:
        recyclerCate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCate.setAdapter(categoryAdapter);

        recyclerGoiY.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerGoiY.setAdapter(productAdapterRating);

        recyclerMuaNhieu.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerMuaNhieu.setAdapter(productAdapterSold);

        // Initializing the ViewPagerAdapter
        bannerAdapter = new BannerAdapter(this, listBanner);
        // Adding the Adapter to the ViewPager
        imgHomeSlider.setSliderAdapter(bannerAdapter);
    }

    public void data() {
        // Danh sách khuyến mãi:
        queryBanner = banRef.limitToFirst(4);
        queryBanner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBanner.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Banner banner = node.getValue(Banner.class);
                    listBanner.add(banner);
                }
                bannerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Danh sách loại sản phẩm:
        cateRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCate.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Category category = node.getValue(Category.class);
                    category.setKey(node.getKey());
                    listCate.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Lọc mua nhiều nhất
        querySortBySold = proRef.orderByChild("sold").limitToLast(4);
        querySortBySold.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProductSold.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    product.setKey(dataSnapshot.getKey());
                    listProductSold.add(product);
                }
                Collections.reverse(listProductSold);
                productAdapterSold.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Các sản phẩm gợi ý:
        querySortBySuggestion = proRef.orderByChild("rating").limitToLast(6);
        querySortBySuggestion.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int max = 0;
                listProductRating.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    product.setKey(dataSnapshot.getKey());
                    listProductRating.add(product);
                }
                Collections.reverse(listProductRating);
                productAdapterRating.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
