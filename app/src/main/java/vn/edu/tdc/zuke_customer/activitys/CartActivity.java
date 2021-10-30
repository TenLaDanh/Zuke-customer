package vn.edu.tdc.zuke_customer.activitys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.CartDetailAdapter;
import vn.edu.tdc.zuke_customer.data_models.Cart;
import vn.edu.tdc.zuke_customer.data_models.CartDetail;
import vn.edu.tdc.zuke_customer.data_models.OfferDetail;
import vn.edu.tdc.zuke_customer.data_models.Product;

public class CartActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView subtitleAppbar;
    ImageView buttonAction;
    String accountID = "abc05684428156";
    RecyclerView cartRecycleView;
    Button btnPayment;
    ArrayList<CartDetail> listCart;
    CartDetailAdapter cartAdapter;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference ref = db.getReference("Cart");
    DatabaseReference detailRef = db.getReference("Cart_Detail");
    int total = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_cart);

        // Toolbar:
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleGH);
        buttonAction = findViewById(R.id.buttonAction);
        buttonAction.setBackground(getResources().getDrawable(R.drawable.ic_round_notifications_24));
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Khởi tạo biến:
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Lấy dữ liệu:
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
                                        total += detail.getPrice();
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
                    break;
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
                                        item.setPrice(priceDiscount);
                                        map.put("price", item.getPrice());
                                        DatabaseReference detailRef = db.getReference("Cart_Detail");
                                        detailRef.child(item.getKey()).updateChildren(map);
                                        cartAdapter.notifyDataSetChanged();
                                    } else {
                                        item.setPrice(product.getPrice());
                                        map.put("price", item.getPrice());
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
            detailRef.child(id).removeValue();
            updateCartTotal(id);
        }
    };

    private String formatPrice(int price) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
    }

    private void updateCartTotal(String cartID) {
        detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot node : snapshot.getChildren()) {
                    CartDetail detail = node.getValue(CartDetail.class);
                    if (detail.getCartID().equals(cartID)) {
                        total += detail.getPrice()*detail.getAmount();
                    }
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("accountID", accountID);
                map.put("total", total);
                ref.child(cartID).updateChildren(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
