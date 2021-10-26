package vn.edu.tdc.zuke_customer.activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.BannerAdapter;
import vn.edu.tdc.zuke_customer.adapters.CategoryAdapter;
import vn.edu.tdc.zuke_customer.adapters.ProductAdapter;
import vn.edu.tdc.zuke_customer.data_models.Catelogy;
import vn.edu.tdc.zuke_customer.data_models.Product;

public class HomeScreenActivity extends AppCompatActivity {
    // Khai báo biến:
    RecyclerView recyclerCate, recyclerGoiY, recyclerMuaNhieu;
    ArrayList<Catelogy> listCate;
    ArrayList<Product> listProduct;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;
    // creating object of ViewPager
    ViewPager mViewPager;
    FirebaseAuth mAuth;

    // images array
    int[] images1 = {R.drawable.a1, R.drawable.a2, R.drawable.a3};

    // Creating Object of ViewPagerAdapter
    BannerAdapter bannerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        mAuth = FirebaseAuth.getInstance();

        listCate = new ArrayList<>();
        listProduct = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, listCate);
        productAdapter = new ProductAdapter(this, listProduct);
        data();
        recyclerCate = findViewById(R.id.recycler_view);
        recyclerCate.setHasFixedSize(true);
        recyclerGoiY = findViewById(R.id.recycler_view1);
        recyclerGoiY.setHasFixedSize(true);
        recyclerMuaNhieu = findViewById(R.id.recycler_view2);
        recyclerMuaNhieu.setHasFixedSize(true);

        // Đổ dữ liệu vào recyclerView:
        recyclerCate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCate.setAdapter(categoryAdapter);

        recyclerGoiY.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerGoiY.setAdapter(productAdapter);

        recyclerMuaNhieu.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerMuaNhieu.setAdapter(productAdapter);

        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);
        // Initializing the ViewPagerAdapter
        bannerAdapter = new BannerAdapter(this, images1);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(bannerAdapter);
    }

    public void data() {
        listCate.add(new Catelogy("1", "Điện thoại", "a"));
        listCate.add(new Catelogy("1", "Máy tính bảng", "a"));
        listCate.add(new Catelogy("1", "Laptop", "a"));
        listCate.add(new Catelogy("1", "Màn hình", "a"));
        listCate.add(new Catelogy("1", "Tivi", "a"));

        listProduct.add(new Product("Product 1", 20000000));
        listProduct.add(new Product("Product 2", 20000000));
        listProduct.add(new Product("Product 3", 20000000));
        listProduct.add(new Product("Product 4", 20000000));
        listProduct.add(new Product("Product 5", 20000000));
        listProduct.add(new Product("Product 6", 20000000));
    }
}
