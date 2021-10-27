package vn.edu.tdc.zuke_customer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.adapters.CartDetailAdapter;
import vn.edu.tdc.zuke_customer.adapters.CartDetailTTAdapter;
import vn.edu.tdc.zuke_customer.data_models.Cart;
import vn.edu.tdc.zuke_customer.data_models.CartDetail;
import vn.edu.tdc.zuke_customer.data_models.Customer;
import vn.edu.tdc.zuke_customer.data_models.DiscountCode;
import vn.edu.tdc.zuke_customer.data_models.DiscountCode_Customer;

public class PaymentActivity extends AppCompatActivity {
    ImageView btnMap;
    Button btnSubmit;
    TextInputEditText edtAddress,edtName,edtPhone,edtDiscountCode,edtNote;
    RecyclerView productRecyclerView;
    TextView txtTotal,txtTransportFee,txtDiscount,txtRemain;
    ArrayList<CartDetail> listCart;
    CartDetailTTAdapter cartAdapter;
    int total = 0;
    String accountID = "-MmcLcAy0lUBiYMA5E8c";
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference cartRef = db.getReference("Cart");
    DatabaseReference detailRef = db.getReference("Cart_Detail");
    DatabaseReference code_cusRef = db.getReference("DiscountCode_Customer");
    DatabaseReference customerRef = db.getReference("Customer");
    DatabaseReference discountcodeRef = db.getReference("DiscountCode");
    String address = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payment);
        //get control
        edtAddress = findViewById(R.id.editTextAddress);
        edtName = findViewById(R.id.editTextName);
        edtPhone = findViewById(R.id.editTextPhone);
        productRecyclerView = findViewById(R.id.listProduct);
        edtDiscountCode = findViewById(R.id.editTextDiscountCode);
        edtNote = findViewById(R.id.editTextMessage);
        txtTotal = findViewById(R.id.txt_tongtien);
        txtRemain = findViewById(R.id.txt_conlai);
        txtTransportFee = findViewById(R.id.txt_phivanchuyen);
        txtDiscount = findViewById(R.id.txt_dathanhtoan);
        btnSubmit = findViewById(R.id.buttonTTXacNhan);
        btnMap = findViewById(R.id.btnMap);
        listCart = new ArrayList<>();
        cartAdapter = new CartDetailTTAdapter(this, listCart);
        if( getIntent().getStringExtra("address") != null){
            address =  getIntent().getStringExtra("address");
            edtAddress.setText(address);
        }


        data();
//        cartAdapter.setItemClickListener(itemClickListener);
        productRecyclerView.setAdapter(cartAdapter);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        edtDiscountCode.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                Log.d("aaa", "afterTextChanged: anything");
                if(!String.valueOf(s).equals("")){
                    customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot node : snapshot.getChildren()){
                                Customer  customer = node.getValue(Customer.class);
                                customer.setKey(node.getKey());
                                if(customer.getAccountID().equals(accountID)){
                                    code_cusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            for(DataSnapshot node1 : snapshot1.getChildren()){
                                                DiscountCode_Customer temp = node1.getValue(DiscountCode_Customer.class);

                                                if(temp.getCustomer_id().equals(customer.getKey())){
                                                    discountcodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                            for(DataSnapshot node2 : snapshot2.getChildren()){
                                                                DiscountCode code = node2.getValue(DiscountCode.class);
                                                                if(code.getCode().equals(String.valueOf(edtDiscountCode.getText()))){
                                                                    if(code.getType().equals("%")){
                                                                        int value = code.getValue();
                                                                        int totalPrice = toPrice(String.valueOf(txtTotal.getText()));
                                                                        int discount = totalPrice / 100 * value;

                                                                        txtDiscount.setText(formatPrice(discount));
                                                                        txtRemain.setText(formatPrice(totalPrice-discount));

                                                                    }else  if(code.getType().equals("VND")){
                                                                        int value = code.getValue();
                                                                        int totalPrice = toPrice(String.valueOf(txtTotal.getText()));
                                                                        int discount =  value;
                                                                        txtDiscount.setText(formatPrice(discount));
                                                                        txtRemain.setText(formatPrice(totalPrice-discount));
                                                                    }
                                                                    else {
                                                                        txtTransportFee.setText(formatPrice(0));
                                                                        // Truong hop la phi van chuyen nua
                                                                        // Thong bao kiem tra lai ma giam gia/ma giam gia k co trong he thong
                                                                    }
                                                                }
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
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });

    }


    private void data() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot node : snapshot.getChildren()){
                    Cart cart = node.getValue(Cart.class);
                    if(cart.getAccountID().equals(accountID)){
                        detailRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                listCart.clear();
                                total = 0;
                                for(DataSnapshot node1 : snapshot1.getChildren()){
                                    CartDetail detail = node1.getValue(CartDetail.class);
                                    if(detail.getCartID().equals(node.getKey())){
                                        listCart.add(detail);
                                        total += detail.getTotalPrice();
                                    }
                                }
                                txtTotal.setText(formatPrice(total));
                                txtRemain.setText(formatPrice(total));
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
        txtDiscount.setText(formatPrice(0));
        txtTransportFee.setText(formatPrice(0));
        txtRemain.setText(formatPrice(total));
    }
    private int checkError(){
        if(String.valueOf(edtAddress.getText()).equals("")){
            //Thông báo "Địa chỉ không được để trống"
            return  -1;
        }
        if(String.valueOf(edtName.getText()).equals("")){
            //Thông báo "Tên người nhận không được để trống"
            return  -1;
        }
        if(String.valueOf(edtPhone.getText()).equals("")){
            //Thông báo "Số điện thoại không được để trống"
            return  -1;
        }
        return 1;
    }
    private String formatPrice(int price) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
    }
    private int toPrice(String price){
        price = price.substring(0, price.length() - 2).replace(".", "");

        int totalPrice = Integer.parseInt(price);
        return totalPrice;
    }
}
