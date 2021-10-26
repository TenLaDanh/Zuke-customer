package vn.edu.tdc.zuke_customer.activitys;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.CartDetailAdapter;
import vn.edu.tdc.zuke_customer.data_models.Cart;
import vn.edu.tdc.zuke_customer.data_models.CartDetail;
import vn.edu.tdc.zuke_customer.data_models.OfferDetail;
import vn.edu.tdc.zuke_customer.data_models.Product;

public class CartActivity extends AppCompatActivity {
    String accountID = "-MmcLcAy0lUBiYMA5E8c";
    RecyclerView cartRecycleView;
    Button btnPayment;
    ArrayList<CartDetail> listCart;
    CartDetailAdapter cartAdapter;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference ref = db.getReference("Cart");
    DatabaseReference detailRef = db.getReference("Cart_Detail");
    DatabaseReference cartRef = db.getReference("Cart_Detail");
    DatabaseReference promoRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
    int total = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_cart);

        btnPayment = findViewById(R.id.buttonThanhToan);

        cartRecycleView = findViewById(R.id.listProduct);
        cartRecycleView.setHasFixedSize(true);
        listCart = new ArrayList<>();
        cartAdapter = new CartDetailAdapter(this, listCart);
        data();
        cartAdapter.setItemClickListener(itemClickListener);
        cartRecycleView.setAdapter(cartAdapter);
        cartRecycleView.setLayoutManager(new LinearLayoutManager(this));

        // Trượt xoá giỏ hàng:
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        cartRecycleView.addItemDecoration(itemDecoration);
    }

    private void data() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Cart cart = node.getValue(Cart.class);
                    cart.setCartID(node.getKey());
                    if (cart.getAccountID().equals(accountID)) {
                        detailRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                listCart.clear();
                                total = 0;
                                for (DataSnapshot node1 : snapshot.getChildren()) {
                                    CartDetail detail = node1.getValue(CartDetail.class);
                                    detail.setKey(node1.getKey());
                                    if (cart.getCartID().equals(detail.getCartID())) {
                                        listCart.add(detail);
                                        total += detail.getTotalPrice();
                                    }
                                }
                                btnPayment.setText("Tổng tiền : " + formatPrice(cart.getTotal()));
                                cartAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private CartDetailAdapter.ItemClickListener itemClickListener = new CartDetailAdapter.ItemClickListener() {
        @Override
        public void changeQuantity(CartDetail item, int value) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("cartID", item.getCartID());
            map.put("amount", value);
            map.put("productID", item.getProductID());
            proRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot node : snapshot.getChildren()) {
                        Product product = node.getValue(Product.class);
                        if (node.getKey().equals(item.getProductID())) {
                            DatabaseReference promoRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
                            promoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int maxSale = 0;
                                    for (DataSnapshot node1 : snapshot.getChildren()) {
                                        OfferDetail detail = node1.getValue(OfferDetail.class);
                                        if (detail.getProductID().equals(item.getProductID())) {
                                            if (detail.getPercentSale() > maxSale) {
                                                maxSale = detail.getPercentSale();
                                            }
                                        }
                                    }
                                    if (maxSale != 0) {
                                        int priceDiscount = product.getPrice() / 100 * (100-maxSale);
                                        item.setTotalPrice(priceDiscount * value);
                                        map.put("totalPrice", item.getTotalPrice());
                                        DatabaseReference detailRef = db.getReference("Cart_Detail");
                                        detailRef.child(item.getKey()).updateChildren(map);
                                        cartAdapter.notifyDataSetChanged();
                                    } else {
                                        item.setTotalPrice(product.getPrice() * value);
                                        map.put("totalPrice", item.getTotalPrice());
                                        DatabaseReference detailRef = db.getReference("Cart_Detail");
                                        detailRef.child(item.getKey()).updateChildren(map);
                                        cartAdapter.notifyDataSetChanged();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //update Cart total
            updateCartTotal(item.getCartID());

        }

        @Override
        public void delete(String id) {
            db.getReference("Cart_Detail").child(id).removeValue();
            updateCartTotal(id);
        }
    };

    private String formatPrice(int price) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
    }

    private void updateCartTotal(String cartID) {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot node : snapshot.getChildren()) {
                    CartDetail detail = node.getValue(CartDetail.class);
                    if (detail.getCartID().equals(cartID)) {
                        total += detail.getTotalPrice();
                    }
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("accountID", accountID);
                map.put("total", total);
                db.getReference("Cart").child(cartID).updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
