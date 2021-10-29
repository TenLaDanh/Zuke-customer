package vn.edu.tdc.zuke_customer.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.Category1Adapter;
import vn.edu.tdc.zuke_customer.adapters.CategoryAdapter;
import vn.edu.tdc.zuke_customer.adapters.ManuProductAdapter;
import vn.edu.tdc.zuke_customer.data_models.Category;
import vn.edu.tdc.zuke_customer.data_models.ManuProduct;
import vn.edu.tdc.zuke_customer.data_models.Manufactures;
import vn.edu.tdc.zuke_customer.data_models.Product;
import vn.edu.tdc.zuke_customer.data_models.Rating;

public class CategoryActivity extends Activity {
    RecyclerView rcvCate, rcvManu;
    ArrayList<Product> listProduct;
    ArrayList<Manufactures> listManu;
    ArrayList<Category> listCate;
    ManuProductAdapter adapterManuProduct;
    Category1Adapter adapterCate;
    ArrayList<ManuProduct> listManuProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference manuRef = db.getReference("Manufactures");
    DatabaseReference cateRef = db.getReference("Categories");
    ArrayList<ArrayList<Product>> listPro = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_category);

        // Khởi tạo biến:
        rcvCate = findViewById(R.id.rcvCate);
        rcvManu = findViewById(R.id.rcvManu);
        listCate = new ArrayList<>();
        listProduct = new ArrayList<>();
        listManu = new ArrayList<>();
        listManuProduct = new ArrayList<>();
        adapterCate = new Category1Adapter(this, listCate);
        adapterManuProduct = new ManuProductAdapter(this, listManuProduct);
        // RecyclerView
        data();
        //Category:
        rcvCate.setHasFixedSize(true);
        rcvCate.setAdapter(adapterCate);
        adapterCate.setItemClickListener(itemClickCategory);
        rcvCate.setLayoutManager(new LinearLayoutManager(this));
        //Manufactors:
        rcvManu.setHasFixedSize(true);
        rcvManu.setAdapter(adapterManuProduct);
        rcvManu.setLayoutManager(new LinearLayoutManager(this));
    }

    private final Category1Adapter.ItemClickCategory itemClickCategory = new Category1Adapter.ItemClickCategory() {
        @Override
        public void changeManu(String categoryID) {
            proRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listPro.clear();
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Product product = node.getValue(Product.class);
                        if (product.getCategory_id().equals(categoryID)) {
                            if (listPro.size() > 0) {
                                boolean check = false;
                                for (int i = 0; i < listPro.size(); i++) {
                                    if (listPro.get(i).get(0).getManu_id().equals(product.getManu_id())) {
                                        listPro.get(i).add(product);
                                        check = true;
                                    }
                                }
                                if(!check) {
                                    ArrayList<Product> temp = new ArrayList<>();
                                    temp.add(product);
                                    listPro.add(temp);
                                }
                            } else {
                                ArrayList<Product> temp = new ArrayList<>();
                                temp.add(product);
                                listPro.add(temp);
                            }
                        }
                    }

                    listManuProduct.clear();
                    for (ArrayList<Product> list : listPro) {
                        manuRef.child(list.get(0).getManu_id()).get().addOnSuccessListener(dataSnapshot -> {
                            String manuName = dataSnapshot.getValue(Manufactures.class).getName();
                            if (list.size() > 0) {
                                listProduct = list;
                                if(listProduct.size() > 4) {
                                    listProduct = new ArrayList<>(list.subList(0, 4));
                                }
                                listManuProduct.add(new ManuProduct(listProduct, manuName));
                                adapterManuProduct.notifyDataSetChanged();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    };

    private void data() {
        cateRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCate.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    category.setKey(dataSnapshot.getKey());
                    listCate.add(category);
                }
                adapterCate.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Ban dau
        proRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPro.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product product = node.getValue(Product.class);
                        if (listPro.size() > 0) {
                            boolean check = false;
                            for (int i = 0; i < listPro.size(); i++) {
                                if (listPro.get(i).get(0).getManu_id().equals(product.getManu_id())) {
                                    listPro.get(i).add(product);
                                    check = true;
                                }
                            }
                            if(!check) {
                                ArrayList<Product> temp = new ArrayList<>();
                                temp.add(product);
                                listPro.add(temp);
                            }
                        } else {
                            ArrayList<Product> temp = new ArrayList<>();
                            temp.add(product);
                            listPro.add(temp);
                        }
                }

                listManuProduct.clear();
                for (ArrayList<Product> list : listPro) {
                    manuRef.child(list.get(0).getManu_id()).get().addOnSuccessListener(dataSnapshot -> {
                        String manuName = dataSnapshot.getValue(Manufactures.class).getName();
                        if (list.size() > 0) {
                            listProduct = list;
                            if(listProduct.size() > 4) {
                                listProduct = new ArrayList<>(list.subList(0, 4));
                            }
                            listManuProduct.add(new ManuProduct(listProduct, manuName));
                            adapterManuProduct.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
