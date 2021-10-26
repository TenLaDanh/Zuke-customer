package vn.edu.tdc.zuke_customer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.data_models.CartDetail;
import vn.edu.tdc.zuke_customer.data_models.OfferDetail;
import vn.edu.tdc.zuke_customer.data_models.Product;

public class CartDetailAdapter extends RecyclerView.Adapter<CartDetailAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CartDetail> list;
    ItemClickListener itemClickListener;
    DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");
    DatabaseReference promoRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
    ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CartDetailAdapter(Context context, ArrayList<CartDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CartDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product_cart, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartDetail item = list.get(position);
        proRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product product = node.getValue(Product.class);
                    if (node.getKey().equals(item.getProductID())) {
                        //set name
                        holder.itemName.setText(product.getName());
                        //set gia san pham
                        holder.itemPrice.setText(formatPrice(product.getPrice()));
                        promoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int maxSale = 0;
                                for(DataSnapshot node1 : snapshot.getChildren()){
                                    OfferDetail detail = node1.getValue(OfferDetail.class);
                                    if(detail.getProductID().equals(item.getProductID())){
                                        if(detail.getPercentSale() > maxSale){
                                            maxSale = detail.getPercentSale();
                                        }
                                    }
                                }
                                if(maxSale != 0){
                                    int discount = product.getPrice() /100 * (100-maxSale);
                                    holder.itemPriceDiscount.setText(formatPrice(discount));
                                    holder.itemPrice.setPaintFlags(holder.itemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                } else {
                                    holder.itemPriceDiscount.setText(formatPrice(product.getPrice()));
                                    holder.itemPrice.setVisibility(View.INVISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.edtValue.setText(""+item.getAmount());
                        //set hinh anh
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        final long ONE_MEGABYTE = 1024 * 1024;
                        StorageReference imageRef = storage.getReference("images/products/" + product.getName() + "/" + product.getImage());
                        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.itemImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, holder.itemImage.getWidth(), holder.itemImage.getHeight(), false));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null) {
                    int value = Integer.parseInt(String.valueOf(holder.edtValue.getText()));
                    if (v == holder.btnAdd) {
                        value++;
                        holder.edtValue.setText(String.valueOf(value));
                        itemClickListener.changeQuantity(item, value);
                    }
                    if (v == holder.btnMinus) {
                        value--;
                        holder.edtValue.setText(String.valueOf(value));
                        itemClickListener.changeQuantity(item, value);
                    }
                    if (v == holder.cardView) itemClickListener.delete(item.getKey());

                } else {
                    return;
                }
            }
        };
        viewBinderHelper.bind(holder.swipeRevealLayout, item.getKey());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SwipeRevealLayout swipeRevealLayout;
        CardView cardView;
        private ImageView itemImage;
        private TextView itemName, itemPrice, itemPriceDiscount;
        private Button btnAdd,btnMinus;
        private EditText edtValue;
        View.OnClickListener onClickListener;

        public ViewHolder(View view) {
            super(view);
            swipeRevealLayout = view.findViewById(R.id.swipelayout);
            cardView = view.findViewById(R.id.cardView);
            itemImage = view.findViewById(R.id.img);
            itemName = view.findViewById(R.id.txt_name);
            itemPrice = view.findViewById(R.id.txt_price);
            itemPriceDiscount = view.findViewById(R.id.txtPriceDiscount);
            itemPriceDiscount.setText("");
            btnAdd = view.findViewById(R.id.plusButton);
            btnMinus = view.findViewById(R.id.minusButton);
            edtValue = view.findViewById(R.id.valueAmount);
            btnMinus.setOnClickListener(this);
            btnAdd.setOnClickListener(this);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void changeQuantity(CartDetail item, int value);
        void delete(String id);
    }
    private String formatPrice(int price) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
    }
}
